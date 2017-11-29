package org.koub.jdt.helpers.scan.ecore.manualcode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IDocElement;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;

public class NonGeneratedMethodAstVisitor extends ASTVisitor {

	private final List<MethodDeclaration> nonGeneratedMethods;

	public NonGeneratedMethodAstVisitor() {
		this.nonGeneratedMethods = new ArrayList<>();
	}

	public final List<MethodDeclaration> getNonGeneratedMethods() {
		return this.nonGeneratedMethods;
	}

	@Override
	public boolean visit(final MethodDeclaration node) {
		boolean isGenerated = false;

		final Javadoc javaDoc = node.getJavadoc();
		if (javaDoc != null) {
			@SuppressWarnings("unchecked")
			final List<TagElement> tags = javaDoc.tags();
			for (TagElement tag : tags) {
				final String tagName = tag.getTagName();
				if ("@generated".equals(tagName)) {
					isGenerated = true;
					@SuppressWarnings("unchecked")
					final List<IDocElement> fragments = tag.fragments();
					for (IDocElement fragment : fragments) {
						if (fragment instanceof TextElement) {
							final String javaDocTagFragmentText = ((TextElement) fragment).getText();
							if (javaDocTagFragmentText != null && javaDocTagFragmentText.toLowerCase().contains("not")) {
								isGenerated = false;
								break;
							}
						}
					}
				}
			}
		}
		if (!isGenerated) {
			this.nonGeneratedMethods.add(node);
		}
		// do not visit the body of the method as it may contain class declaration with
		// method not tagged as generated, where they are actually generated.
		return false;
	}
}
