import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.completion {
    CompletionProgressIndicator,
    CompletionService
}
import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementPresentation,
    LookupManager
}
import com.intellij.codeInsight.lookup.impl {
    EmptyLookupItem,
    LookupImpl,
    LookupCellRenderer
}
import com.intellij.ide.util.treeView {
    PresentableNodeDescriptor
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.ui {
    JBColor,
    SimpleColoredComponent,
    SimpleTextAttributes
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails
}
import com.redhat.ceylon.ide.common.util {
    escaping
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Module
}

import java.awt {
    Color,
    Component,
    Insets
}
import java.lang {
    ReflectiveOperationException,
    Runnable
}

import javax.swing {
    JPanel,
    Icon,
    JList,
    ListCellRenderer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    textAttributes,
    ceylonHighlightingColors
}

shared void installCustomLookupCellRenderer(Project project) {
    if (is CompletionProgressIndicator currentCompletion
            = CompletionService.completionService.currentCompletion) {
        CustomLookupCellRenderer(currentCompletion.lookup, project).install();
    }
    else if (is LookupImpl activeLookup
            = LookupManager.getInstance(project).activeLookup) {
        CustomLookupCellRenderer(activeLookup, project).install();
    }
}

shared alias Fragment => PresentableNodeDescriptor<Anything>.ColoredFragment;
Fragment createFragment(String text, SimpleTextAttributes atts)
        => PresentableNodeDescriptor<Anything>.ColoredFragment(text, atts);

shared class CustomLookupCellRenderer(LookupImpl lookup, Project project)
        extends LookupCellRenderer(lookup) {

    function brighter(SimpleTextAttributes textAttributes)
            => let (fg = textAttributes.fgColor)
                SimpleTextAttributes(
                    textAttributes.bgColor,
                    fg.red+fg.green+fg.blue>=384
                        then fg.darker().darker()
                        else fg.brighter().brighter(),
                    textAttributes.waveColor,
                    textAttributes.style);

    value searchMatch
            = SimpleTextAttributes(SimpleTextAttributes.styleSearchMatch, Color.black);

    function highlighted(Fragment fragment, Boolean selected)
            => selected then searchMatch else brighter(fragment.attributes);

    shared void install()
            => ApplicationManager.application
                .invokeLater(object satisfies Runnable {
            shared actual void run() {
                try {
                    value field
                            = javaClass<LookupImpl>()
                            .getDeclaredField("myCellRenderer");
                    field.accessible = true;
                    field.set(lookup, outer);
                    field.accessible = false;
                    value method
                            = javaClass<JList<out Object>>()
                            .getDeclaredMethod("setCellRenderer",
                                javaClass<ListCellRenderer<out Object>>());
                    method.invoke(lookup.list, outer);
                }
                catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        });

    shared actual Component getListCellRendererComponent(JList<out Object>? list,
            Object? element, Integer index, Boolean isSelected, Boolean hasFocus) {
        Component component
                = super.getListCellRendererComponent(list, element, index, isSelected, hasFocus);
        assert (is LookupElement element);
        customize(component, element, isSelected);
        return component;
    }

    void customize(Component comp, LookupElement element, Boolean isSelected) {
        if (is JPanel comp,
            is SimpleColoredComponent coloredComponent = comp.getComponent(0)) {
            value pres = LookupElementPresentation();
            element.renderElement(pres);
            assert (exists text = pres.itemText);
            try {
                resetColoredComponent(coloredComponent);
                renderItemName {
                    item = element;
                    selected = isSelected && lookup.focused;
                    name = text;
                    nameComponent = coloredComponent;
                    strikeout = pres.strikeout;
                };
            }
            catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    Color color(String token, Boolean qualifiedNameIsPath) {
        if (token in escaping.keywords) {
            return textAttributes(ceylonHighlightingColors.keyword).foregroundColor;
        }
        else if (token.every(Character.whitespace)
              || token.size==1 && token in "()[]{}<>,.+*&|?;= "
              || token == "...") {
            return JBColor.foreground();
        }
        else if (token.startsWith("\"") && token.endsWith("\"")) {
            return textAttributes(ceylonHighlightingColors.strings).foregroundColor;
        }
        else {
            assert (exists first = token[0]);
            value key
                = if (qualifiedNameIsPath) then ceylonHighlightingColors.packages
                else if (first.uppercase) then ceylonHighlightingColors.type
                else ceylonHighlightingColors.identifier;
            return textAttributes(key).foregroundColor;
        }
    }

    Fragment[] colorizeTokens(Boolean selected, String text, Integer style, Boolean qualifiedNameIsPath) {
        if (selected) {
            return Singleton(createFragment(text, SimpleTextAttributes(style, JBColor.white)));
        }
        else if (text.startsWith("shared actual ")) {
            value color = textAttributes(ceylonHighlightingColors.annotation).foregroundColor;
            return [
                createFragment("shared actual ", SimpleTextAttributes(style, color)),
                *colorizeTokens(selected, text[14...], style, qualifiedNameIsPath)
            ];
        }
        else {
            value pattern = qualifiedNameIsPath then "()[]{}<>,+*&|?;= " else "()[]{}<>,.+*&|?;= ";
            return [
                for (token in text.split(pattern.contains, false, false)) if (!token.empty)
                createFragment(token, SimpleTextAttributes(style, color(token, qualifiedNameIsPath)))
            ];

        }
    }

    void renderItemName(LookupElement item, Boolean selected, String name,
            SimpleColoredComponent nameComponent, Boolean strikeout) {

        value colors
                = colorizeTokens {
                    text = name;
                    selected = selected;
                    style = strikeout
                        then SimpleTextAttributes.styleStrikeout
                        else SimpleTextAttributes.stylePlain;
                    qualifiedNameIsPath
                        = item.\iobject
                        is ModuleVersionDetails|Package|Module;
                };

        String prefix
                = item is EmptyLookupItem
                then ""
                else lookup.itemPattern(item);

        value colorsWithPrefix
                = if (/*selected &&*/ prefix.size>0,
                      exists ranges = getMatchingFragments(prefix, item.lookupString))
                then let (it = ranges.iterator())
                mergeHighlightAndMatches {
                    highlight = colors;
                    selected = selected;
                    from = name.firstInclusion(item.lookupString) else 0;
                    nextMatch() => it.hasNext() then it.next();
                }
                else colors;

        for (color in colorsWithPrefix) {
            nameComponent.append(color.text, color.attributes);
        }
    }

    void resetColoredComponent(SimpleColoredComponent coloredComponent) {
        Icon icon = coloredComponent.icon;
        Insets ipad = coloredComponent.ipad;
        coloredComponent.clear();
        coloredComponent.setIcon(icon);
        coloredComponent.ipad = ipad;
    }

    shared List<Fragment> mergeHighlightAndMatches(List<Fragment> highlight,
            Integer from, TextRange? nextMatch(), Boolean selected) {

        value merged = ArrayList<Fragment>();
        variable value currentRange = nextMatch();
        variable Integer currentIndex = 0;
        for (fragment in highlight) {
            value text = fragment.text;
            value size = text.size;
            value initialRange = currentRange;
            if (!exists initialRange) {
                merged.add(fragment);
            }
            else if (currentIndex + size <= initialRange.startOffset + from) {
                merged.add(fragment);
            }
            else {
                variable Integer substart = 0;
                variable Integer sublength = 0;
                variable Integer consumedFromRange = 0;
                while (exists range = currentRange) {

                    if (currentIndex < range.startOffset + from) {
                        sublength = range.startOffset + from - currentIndex;
                        String subtext = text.substring(substart, sublength);
                        merged.add(createFragment(subtext, fragment.attributes));
                    }

                    if (range.endOffset + from > currentIndex + size) {
                        String subtext = text[sublength...];
                        merged.add(createFragment(subtext, highlighted(fragment, selected)));
                        consumedFromRange += size - sublength;
                        currentRange = null;
                    }
                    else {

                        String subtext;
                        if (consumedFromRange > 0) {
                            Integer toConsume = range.length - consumedFromRange;
                            subtext = text[0:toConsume];
                            sublength = toConsume;
                        }
                        else {
                            subtext = text[sublength:range.length];
                            sublength += range.length;
                        }
                        merged.add(createFragment(subtext, highlighted(fragment, selected)));

                        value nextRange = nextMatch();
                        consumedFromRange = 0;
                        if (exists nextRange,
                            nextRange.startOffset + from < currentIndex + size) {
                            currentRange = nextRange;
                        }
                        else {
                            if (sublength < size) {
                                String rest = text[sublength...];
                                merged.add(createFragment(rest, fragment.attributes));
                            }
                            currentRange = null;
                        }

                    }
                    substart = sublength;

                }
            }
            currentIndex += size;

        }
        return merged;
    }

}
