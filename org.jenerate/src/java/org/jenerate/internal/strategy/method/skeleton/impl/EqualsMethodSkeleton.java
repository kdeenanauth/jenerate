package org.jenerate.internal.strategy.method.skeleton.impl;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jenerate.internal.domain.data.EqualsHashCodeGenerationData;
import org.jenerate.internal.domain.identifier.impl.MethodsGenerationCommandIdentifier;
import org.jenerate.internal.manage.PreferencesManager;
import org.jenerate.internal.strategy.method.skeleton.MethodSkeleton;

/**
 * Specific implementation of the {@link MethodSkeleton} for the equals method.
 * 
 * @author maudrain
 */
public class EqualsMethodSkeleton extends AbstractMethodSkeleton<EqualsHashCodeGenerationData> {

    /**
     * Public for testing purpose
     */
    public static final String EQUALS_METHOD_NAME = "equals";

    public EqualsMethodSkeleton(PreferencesManager preferencesManager) {
        super(preferencesManager);
    }

    @Override
    public String getMethod(IType objectClass, EqualsHashCodeGenerationData data, String methodContent)
            throws JavaModelException {
        boolean addOverride = addOverride(objectClass);
        return createEqualsMethod(data, addOverride, methodContent);
    }

    @Override
    public MethodsGenerationCommandIdentifier getCommandIdentifier() {
        return MethodsGenerationCommandIdentifier.EQUALS_HASH_CODE;
    }

    @Override
    public String getMethodName() {
        return EQUALS_METHOD_NAME;
    }

    @Override
    public String[] getMethodArguments(IType objectClass) throws Exception {
        return new String[] { "QObject;" };
    }

    private String createEqualsMethod(EqualsHashCodeGenerationData data, boolean addOverride, String methodContent) {

        StringBuffer content = new StringBuffer();
        if (data.generateComment()) {
            content.append("/**\n");
            content.append(" * {@inheritDoc}\n *\n");
            content.append(" * @see java.lang.Object#equals(java.lang.Object)\n");
            content.append(" */\n");
        }
        if (addOverride) {
            content.append("@Override\n");
        }
        content.append("public boolean equals(final Object obj) {\n");
        content.append(methodContent);
        content.append("}\n\n");

        return content.toString();
    }
}
