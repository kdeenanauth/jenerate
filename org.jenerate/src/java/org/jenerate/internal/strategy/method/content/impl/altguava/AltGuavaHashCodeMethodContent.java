package org.jenerate.internal.strategy.method.content.impl.altguava;

import java.util.LinkedHashSet;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.jenerate.internal.domain.data.EqualsHashCodeGenerationData;
import org.jenerate.internal.domain.identifier.impl.MethodContentStrategyIdentifier;
import org.jenerate.internal.manage.PreferencesManager;
import org.jenerate.internal.strategy.method.content.MethodContent;
import org.jenerate.internal.strategy.method.content.impl.AbstractMethodContent;
import org.jenerate.internal.strategy.method.content.impl.MethodContentGenerations;
import org.jenerate.internal.strategy.method.skeleton.impl.HashCodeMethodSkeleton;

/**
 * Specific implementation of the {@link MethodContent} for {@link HashCodeMethodSkeleton} using guava.
 * 
 * @author maudrain
 */
public class AltGuavaHashCodeMethodContent extends
        AbstractMethodContent<HashCodeMethodSkeleton, EqualsHashCodeGenerationData> {

    /**
     * Public for testing purpose
     */
    public static final String LIBRARY_TO_IMPORT = "com.google.common.base.Objects";

    public AltGuavaHashCodeMethodContent(PreferencesManager preferencesManager) {
        super(MethodContentStrategyIdentifier.USE_ALTGUAVA, preferencesManager);
    }

    @Override
    public String getMethodContent(IType objectClass, EqualsHashCodeGenerationData data) throws Exception {
        StringBuffer content = new StringBuffer();
        content.append("return Objects.hashCode(");
        if (data.appendSuper()) {
            content.append("super.hashCode(), ");
        }
        IField[] checkedFields = data.getCheckedFields();
        
        int i=0;
        for (IField checkedField : checkedFields) {
            content.append(MethodContentGenerations.getFieldAccessorString(checkedField,
                    data.useGettersInsteadOfFields()));
            
            // append new line every 6 arguments
            if (i != 0 && i != checkedFields.length-1 && i % 6 == 0) {
            	content.append(", //\n");
            } else if(i != checkedFields.length-1) {
            	content.append(", ");
            }
            i++;
        }
        content.append(");\n");
        return content.toString();
    }

    @Override
    public LinkedHashSet<String> getLibrariesToImport(EqualsHashCodeGenerationData data) {
        LinkedHashSet<String> toReturn = new LinkedHashSet<String>();
        toReturn.add(LIBRARY_TO_IMPORT);
        return toReturn;
    }

    @Override
    public Class<HashCodeMethodSkeleton> getRelatedMethodSkeletonClass() {
        return HashCodeMethodSkeleton.class;
    }

}
