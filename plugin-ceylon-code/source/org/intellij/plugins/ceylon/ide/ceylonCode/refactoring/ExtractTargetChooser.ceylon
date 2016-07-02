import com.intellij.codeInsight.unwrap {
    ScopeHighlighter
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.ui.popup {
    JBPopupFactory,
    LightweightWindowEvent,
    JBPopupAdapter
}
import com.intellij.psi {
    PsiElement,
    SmartPointerManager,
    SmartPsiElementPointer
}
import com.intellij.ui {
    JBColor {
        red=RED
    }
}
import com.intellij.ui.components {
    JBList
}

import java.awt {
    Component
}
import java.lang {
    Runnable
}
import java.util {
    Collections
}

import javax.swing {
    DefaultListModel,
    DefaultListCellRenderer,
    JList,
    ListSelectionModel
}
import javax.swing.event {
    ListSelectionListener,
    ListSelectionEvent
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}

shared void showChooser<out T>
        (Editor editor, {T*} expressions, String title, Integer? selection = null)
        (void callback(T t), String renderer(T t))
        given T satisfies PsiElement {

    //highlights the selected element in the editor
    value rangeHighlighter = ScopeHighlighter(editor, ScopeHighlighter.naturalRanger);

    value model = DefaultListModel<SmartPsiElementPointer<T>>();
    for (expr in expressions) {
        model.addElement(SmartPointerManager.getInstance(expr.project).createSmartPsiElementPointer(expr));
    }
    JBList myList = JBList(model);

    myList.selectionModel.selectionMode = ListSelectionModel.singleSelection;
    if (exists selection) {
        myList.selectedIndex = selection;
    }

    myList.setCellRenderer(object extends DefaultListCellRenderer() {
        shared actual Component getListCellRendererComponent(JList<out Object> list,
                Object \ivalue, Integer index, Boolean isSelected, Boolean cellHasFocus) {
            Component rendererComponent =
                super.getListCellRendererComponent(list, \ivalue, index, isSelected, cellHasFocus);
            assert (is SmartPsiElementPointer<out Anything> pointer = \ivalue);
            if (is T expr = pointer.element) {
                value text = renderer(expr).normalized;
                value rawText = text.longerThan(100) then text.spanTo(100) + "..." else text;
                if (isSelected) {
                    this.text = rawText;
                }
                else {
                    this.text =
                        "<html>``highlighter.highlight {
                            rawText = rawText;
                            project = expr.project;
                            qualifiedNameIsPath = text.startsWith("package");
                        }``</html>";
                }
            } else {
                this.setForeground(red);
                this.text = "Invalid";
            }
            return rendererComponent;
        }
    });

    myList.addListSelectionListener(object satisfies ListSelectionListener {
        shared actual void valueChanged(ListSelectionEvent e) {
            rangeHighlighter.dropHighlight();
            value index = myList.selectedIndex;
            if (index>=0) {
                if (is PsiElement expr = model.get(index)?.element) {
                    rangeHighlighter.highlight(expr,
                        Collections.singletonList<PsiElement>(expr));
                }
            }
        }
    });

    JBPopupFactory.instance
        .createListPopupBuilder(myList)
        .setTitle(title)
        .setMovable(true)
        .setResizable(true)
        .setRequestFocus(true)
        .setItemChoosenCallback(object satisfies Runnable {
            shared actual void run() {
                assert (is SmartPsiElementPointer<out Anything>? pointer = myList.selectedValue);
                if (is T expr = pointer?.element) {
                    callback(expr);
                }
            }
        })
        .addListener(object extends JBPopupAdapter() {
            onClosed(LightweightWindowEvent event) => rangeHighlighter.dropHighlight();
        })
        .createPopup()
        .showInBestPositionFor(editor);
}

