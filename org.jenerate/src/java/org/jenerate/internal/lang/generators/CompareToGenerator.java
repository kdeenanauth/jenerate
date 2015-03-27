// $Id$
package org.jenerate.internal.lang.generators;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.jenerate.internal.domain.data.CompareToGenerationData;
import org.jenerate.internal.domain.preference.impl.JeneratePreference;
import org.jenerate.internal.generate.method.util.JavaCodeFormatter;
import org.jenerate.internal.generate.method.util.JavaUiCodeAppender;
import org.jenerate.internal.manage.PreferencesManager;
import org.jenerate.internal.strategy.method.content.impl.commonslang.CommonsLangCompareToMethodContent;
import org.jenerate.internal.strategy.method.skeleton.impl.CompareToMethodSkeleton;
import org.jenerate.internal.ui.dialogs.factory.DialogFactory;
import org.jenerate.internal.ui.dialogs.impl.CompareToDialog;
import org.jenerate.internal.util.GeneratorsCommonMethodsDelegate;
import org.jenerate.internal.util.JavaInterfaceCodeAppender;

/**
 * @author jiayun, maudrain
 */
public final class CompareToGenerator implements ILangGenerator {

    private final JavaUiCodeAppender javaUiCodeAppender;
    private final PreferencesManager preferencesManager;
    private final DialogFactory<CompareToDialog, CompareToGenerationData> dialogProvider;
    private final JavaCodeFormatter jeneratePluginCodeFormatter;
    private final GeneratorsCommonMethodsDelegate generatorsCommonMethodsDelegate;
    private final JavaInterfaceCodeAppender javaInterfaceCodeAppender;

    public CompareToGenerator(JavaUiCodeAppender javaUiCodeAppender, PreferencesManager preferencesManager,
            DialogFactory<CompareToDialog, CompareToGenerationData> dialogProvider,
            JavaCodeFormatter jeneratePluginCodeFormatter,
            GeneratorsCommonMethodsDelegate generatorsCommonMethodsDelegate,
            JavaInterfaceCodeAppender javaInterfaceCodeAppender) {
        this.javaUiCodeAppender = javaUiCodeAppender;
        this.preferencesManager = preferencesManager;
        this.dialogProvider = dialogProvider;
        this.jeneratePluginCodeFormatter = jeneratePluginCodeFormatter;
        this.generatorsCommonMethodsDelegate = generatorsCommonMethodsDelegate;
        this.javaInterfaceCodeAppender = javaInterfaceCodeAppender;
    }

    @Override
    public void generate(Shell parentShell, IType objectClass) {
        Set<IMethod> excludedMethods = getExcludedMethods(objectClass);
        try {

            CompareToDialog dialog = dialogProvider.createDialog(parentShell, objectClass, excludedMethods);
            int returnCode = dialog.open();
            if (returnCode == Window.OK) {

                for (IMethod excludedMethod : excludedMethods) {
                    if (excludedMethod.exists()) {
                        excludedMethod.delete(true, null);
                    }
                }

                generateCode(parentShell, objectClass, dialog.getData());
            }

        } catch (Exception e) {
            MessageDialog.openError(parentShell, "MethodSkeleton Generation Failed", e.getMessage());
        }

    }

    private Set<IMethod> getExcludedMethods(IType objectClass) {
        Set<IMethod> excludedMethods = new HashSet<>();

        IMethod existingMethod = objectClass.getMethod("compareTo", new String[] { "QObject;" });

        if (!existingMethod.exists()) {
            existingMethod = objectClass.getMethod("compareTo",
                    new String[] { "Q" + objectClass.getElementName() + ";" });
        }

        if (existingMethod.exists()) {
            excludedMethods.add(existingMethod);
        }
        return excludedMethods;
    }

    private void generateCode(final Shell parentShell, final IType objectClass, CompareToGenerationData data)
            throws Exception {

        boolean useCommonLang3 = ((Boolean) preferencesManager
                .getCurrentPreferenceValue(JeneratePreference.USE_COMMONS_LANG3)).booleanValue();

        CommonsLangCompareToMethodContent compareToMethodContent = null;
        // new CommonsLangCompareToMethodContent(
        // preferencesManager, generatorsCommonMethodsDelegate, useCommonLang3, javaInterfaceCodeAppender);
        String methodContent = compareToMethodContent.getMethodContent(objectClass, data);
        CompareToMethodSkeleton compareToMethod = new CompareToMethodSkeleton(preferencesManager, generatorsCommonMethodsDelegate,
                javaInterfaceCodeAppender);
        String source = compareToMethod.getMethod(objectClass, data, methodContent);

        for (String libraryToImport : compareToMethod.getLibrariesToImport()) {
            objectClass.getCompilationUnit().createImport(libraryToImport, null, null);
        }
        for (String libraryToImport : compareToMethodContent.getLibrariesToImport(data)) {
            objectClass.getCompilationUnit().createImport(libraryToImport, null, null);
        }

        String formattedContent = format(parentShell, objectClass, source);
        IMethod created = objectClass.createMethod(formattedContent, data.getElementPosition(), true, null);

        javaUiCodeAppender.revealInEditor(objectClass, created);
    }

    private String format(final Shell parentShell, final IType objectClass, String source) throws JavaModelException {
        try {
            return jeneratePluginCodeFormatter.formatCode(objectClass, source);
        } catch (Exception e) {
            MessageDialog.openError(parentShell, "Error", e.getMessage());
            return "";
        }
    }

}
