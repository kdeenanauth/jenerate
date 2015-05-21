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
import org.jenerate.internal.strategy.method.skeleton.impl.EqualsMethodSkeleton;

/**
 * Specific implementation of the {@link MethodContent} for {@link EqualsMethodSkeleton} using guava.
 * 
 * @author maudrain
 */
public class AltGuavaEqualsMethodContent extends AbstractMethodContent<EqualsMethodSkeleton, EqualsHashCodeGenerationData> {

    /**
     * Public for testing purpose
     */
    public static final String LIBRARY_TO_IMPORT = "com.google.common.base.Objects";

    public AltGuavaEqualsMethodContent(PreferencesManager preferencesManager) {
        super(MethodContentStrategyIdentifier.USE_ALTGUAVA, preferencesManager);
    }

    @Override
    public String getMethodContent(IType objectClass, EqualsHashCodeGenerationData data) throws Exception {
        StringBuffer content = new StringBuffer();
        String elementName = objectClass.getElementName();
        boolean useBlockInIfStatements = data.useBlockInIfStatements();
        content.append(AltGuavaEqualsMethodContent.createEqualsContentPrefix(data, objectClass));
        if (data.appendSuper()) {
            content.append("if (!super.equals(other))");
            content.append(useBlockInIfStatements ? "{\n" : "");
            content.append(" return false;");
            content.append(useBlockInIfStatements ? "\n}\n" : "");
        }
        content.append(elementName);
        content.append(" other = (");
        content.append(elementName);
        content.append(") obj;\n\n");
        content.append("return ");
        String prefix = "";
        IField[] checkedFields = data.getCheckedFields();
        
        for (int i=0; i < checkedFields.length; i++) {
        	IField checkedField = checkedFields[i];
        	
            content.append(prefix);
            prefix = "&& ";
            content.append("Objects.equal(");
            String fieldName = MethodContentGenerations.getFieldAccessorString(checkedField,
                    data.useGettersInsteadOfFields());
            content.append(fieldName);
            content.append(", other.");
            content.append(fieldName);
            content.append(")");
            
            if (i < checkedFields.length - 1) {
            	content.append(" //\n");
            }
        }
        content.append(";\n");
        return content.toString();
    }
    
    /**
     * Creates the equals method content prefix
     * 
     * @param data the data to extract configuration from
     * @param objectClass the class where the equals method is being generated
     * @return the equals method prefix
     */
    public static String createEqualsContentPrefix(EqualsHashCodeGenerationData data, IType objectClass) {
        StringBuffer content = new StringBuffer();
        boolean useBlockInIfStatements = data.useBlockInIfStatements();
        if (data.compareReferences()) {
            content.append("if (this == obj)");
            content.append(useBlockInIfStatements ? "{\n" : "");
            content.append(" return true;");
            content.append(useBlockInIfStatements ? "\n}\n\n" : "");
        }

        if (data.useClassComparison()) {
            content.append("if (obj == null || getClass() != obj.getClass())");
            content.append(useBlockInIfStatements ? "{\n" : "");
            content.append(" return false;");
            content.append(useBlockInIfStatements ? "\n}\n\n" : "");
        } else {
            content.append("if ( !(obj instanceof ");
            content.append(objectClass.getElementName());
            content.append(") )");
            content.append(useBlockInIfStatements ? "{\n" : "");
            content.append(" return false;");
            content.append(useBlockInIfStatements ? "\n}\n\n" : "");
        }
        return content.toString();
    }

    @Override
    public LinkedHashSet<String> getLibrariesToImport(EqualsHashCodeGenerationData data) {
        LinkedHashSet<String> toReturn = new LinkedHashSet<String>();
        toReturn.add(LIBRARY_TO_IMPORT);
        return toReturn;
    }

    @Override
    public Class<EqualsMethodSkeleton> getRelatedMethodSkeletonClass() {
        return EqualsMethodSkeleton.class;
    }

}
