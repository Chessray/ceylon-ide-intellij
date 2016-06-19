import ceylon.collection {
    ArrayList
}

import com.intellij.icons {
    AllIcons
}
import com.intellij.openapi.util {
    IconLoader
}
import com.intellij.ui {
    RowIcon,
    LayeredIcon
}
import com.intellij.util {
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.model {
    AnyModifiableSourceFile
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Class,
    Interface,
    Function,
    Value,
    ModelUtil,
    TypeParameter,
    TypeAlias,
    NothingType,
    Setter,
    Constructor
}

import javax.swing {
    Icon
}

shared object icons {
    
    shared Icon imports => IconLoader.getIcon("/icons/ceylonImports.png");
    shared Icon singleImport => IconLoader.getIcon("/icons/ceylonImport.png");
//    shared Icon packages => IconLoader.getIcon("/icons/ceylonPackage.png");
    shared Icon packageFolders => PlatformIcons.packageIcon;
    shared Icon packageArchives => PlatformIcons.packageIcon;
    shared Icon moduleFolders => IconLoader.getIcon("/icons/moduleFolder.png");
    shared Icon moduleArchives => IconLoader.getIcon("/icons/moduleArchive.png");
    shared Icon descriptors => IconLoader.getIcon("/icons/descriptor.png");
    shared Icon classes => PlatformIcons.classIcon;
    shared Icon abstractClasses => PlatformIcons.abstractClassIcon;
    shared Icon interfaces => PlatformIcons.interfaceIcon;
    shared Icon objects => PlatformIcons.anonymousClassIcon;
    shared Icon methods => PlatformIcons.methodIcon;
    shared Icon formalMethods => PlatformIcons.abstractMethodIcon;
    shared Icon attributes = PlatformIcons.fieldIcon;
    shared Icon enumerations = PlatformIcons.enumIcon;
    shared Icon exceptions = AllIcons.Nodes.exceptionClass;
    shared Icon abstractExceptions = AllIcons.Nodes.abstractException;
    shared Icon annotationClasses = PlatformIcons.annotationTypeIcon;
    shared Icon param => PlatformIcons.parameterIcon;
    shared Icon local => IconLoader.getIcon("/icons/ceylonLocal.png");
    shared Icon values => PlatformIcons.variableIcon;
    shared Icon formalValues => IconLoader.getIcon("/icons/formalValue.png");
    shared Icon anonymousFunction => AllIcons.Nodes.\ifunction;
    shared Icon constructors => PlatformIcons.classInitializer;
    shared Icon setters => PlatformIcons.classInitializer;
    shared Icon annotations => AllIcons.Gutter.extAnnotation;

    shared Icon refinement => AllIcons.Gutter.implementingMethod;
    shared Icon extendedType => AllIcons.Gutter.overridingMethod;
    shared Icon satisfiedTypes => AllIcons.General.implementingMethod;
    shared Icon types => IconLoader.getIcon("/icons/ceylonTypes.png");
    
    shared Icon surround => IconLoader.getIcon("/icons/ceylonSurround.png");
    shared Icon correction => AllIcons.Actions.redo;
    shared Icon addCorrection => AllIcons.General.add;
    shared Icon see => AllIcons.Actions.share;
    shared Icon returns => AllIcons.Actions.stepOut;
    
    shared Icon ceylon => IconLoader.getIcon("/icons/ceylon.png");
    shared Icon file => IconLoader.getIcon("/icons/ceylonFile.png");

    shared Icon problemsViewOk => IconLoader.getIcon("/icons/ceylonProblemsOk.png");
    shared Icon problemsViewErrors => IconLoader.getIcon("/icons/ceylonProblemsErrors.png");
    shared Icon problemsViewWarnings => IconLoader.getIcon("/icons/ceylonProblemsWarnings.png");

    shared Icon? getBaseIcon(Tree.Declaration|Tree.SpecifierStatement|Declaration obj) {
        if (is Tree.SpecifierStatement obj, !obj.refinement) {
            return null;
        }
        value decl 
            = switch (obj) 
            case (is Tree.Declaration) (obj.declarationModel else obj)
            case (is Tree.SpecifierStatement) (obj.declaration else obj)
            else obj;
        return switch (decl)
            //models:
            case (is Interface)
                interfaces
            case (is Class)
                if (decl.objectClass) then objects
                else if (decl.inherits(decl.unit.throwableDeclaration))
                    then (decl.abstract then abstractExceptions else exceptions)
                else if (decl.annotation) then annotationClasses
                else if (decl.abstract) then abstractClasses
                else classes
            case (is Function)
                if (ModelUtil.isConstructor(decl)) then constructors
                else if (decl.parameter) then param
                else if (decl.formal) then formalMethods
                else methods
            case (is Value)
                if (ModelUtil.isConstructor(decl)) then constructors
                else if (ModelUtil.isObject(decl)) then objects
                else if (decl.parameter) then param
                else if (decl.formal) then formalValues
                else values
            case (is Setter)
                setters
            case (is Constructor) constructors
            case (is TypeAlias|NothingType)
                types
            case (is TypeParameter)
                param // TODO wrong!
            //AST nodes:
            case (is Tree.AnyClass)
                classes
            case (is Tree.AnyInterface)
                interfaces
            case (is Tree.AnyMethod)
                methods
            case (is Tree.AnyAttribute|Tree.Variable)
                values
            case (is Tree.ObjectDefinition)
                objects
            case (is Tree.Constructor|Tree.Enumerated)
                constructors
            case (is Tree.TypeAliasDeclaration)
                types
            case (is Tree.SpecifierStatement)
                (decl.baseMemberExpression is Tree.StaticMemberOrTypeExpression
                    then values else methods)
            else
                null;
    }
    
    shared Icon? forDeclaration(Tree.Declaration|Tree.SpecifierStatement|Declaration obj) {
        value baseIcon = getBaseIcon(obj);
        if (!exists baseIcon) {
            print("Missing icon for ``obj``");
            return null;
        }

        Declaration? model = 
            switch (obj) 
            case (is Tree.Declaration) obj.declarationModel
            case (is Tree.SpecifierStatement) obj.declaration
            else obj;
        
        if (exists model) {
            value decorations = ArrayList<Icon>();
            value layer 
                = if (model.shared) 
                then PlatformIcons.publicIcon
                else PlatformIcons.privateIcon;
            value final 
                = switch (model)
                case (is Class) model.final
//                case (is FunctionOrValue) model.classOrInterfaceMember && !model.actual
                else false;
            if (final) {
                decorations.add(AllIcons.Nodes.finalMark);
            }
            value readonly
                = model.toplevel 
                && !model.unit is AnyModifiableSourceFile;
            if (readonly) {
                decorations.add(PlatformIcons.lockedIcon);
            }
            return createIcon(decorations, baseIcon, layer);
        }
        else if (is Tree.Declaration obj) {
            for (a in obj.annotationList.annotations) {
                if (a.primary.token.text=="shared") {
                    return createIcon([], baseIcon, PlatformIcons.publicIcon);
                }
            }
            return createIcon([], baseIcon, PlatformIcons.privateIcon);
        }
        else if (is Tree.SpecifierStatement obj) {
            return createIcon([], baseIcon, PlatformIcons.publicIcon);
        }
        else {
            return baseIcon;
        }
    }

    Icon createIcon(List<Icon> decorations, Icon icon, Icon visibility) 
            => RowIcon(LayeredIcon(icon, *decorations), visibility);
}
