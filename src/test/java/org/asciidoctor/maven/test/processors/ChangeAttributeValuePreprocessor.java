package org.asciidoctor.maven.test.processors;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.PreprocessorReader;

import java.util.Map;

public class ChangeAttributeValuePreprocessor extends Preprocessor {

    public static final String AUTHOR_NAME = "ThisIsMe";

    public ChangeAttributeValuePreprocessor(Map<String, Object> config) {
        super(config);
        System.out.println(this.getClass().getSimpleName() + "("
                + this.getClass().getSuperclass().getSimpleName() + ") initialized");
    }

    @Override
    public void process(Document document, PreprocessorReader reader) {
        System.out.println("Processing " + this.getClass().getSimpleName());
        System.out.println("Processing: blocks found: " + document.getBlocks().size());
        document.getAttributes().put("author", AUTHOR_NAME);
    }

}
