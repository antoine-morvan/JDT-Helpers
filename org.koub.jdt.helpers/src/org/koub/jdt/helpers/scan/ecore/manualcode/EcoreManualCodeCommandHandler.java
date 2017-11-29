package org.koub.jdt.helpers.scan.ecore.manualcode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;

public class EcoreManualCodeCommandHandler extends AbstractHandler {

	public static final String COMMAND_ID = "org.koub.jdt.helpers.scan.ecore.manualcode.command";

	private final Map<CompilationUnit, List<MethodDeclaration>> nonGeneratedMethods;

	public EcoreManualCodeCommandHandler() {
		this.nonGeneratedMethods = new HashMap<>();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		this.nonGeneratedMethods.clear();
		final ISelection activeMenuSelection = HandlerUtil.getActiveMenuSelection(event);
		if (activeMenuSelection instanceof ITreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection) activeMenuSelection;

			@SuppressWarnings("unchecked")
			final List<IJavaElement> list = treeSelection.toList();
			for (IJavaElement e : list) {
				try {
					visit(e);
				} catch (JavaModelException ex) {
					throw new EcoreManualCodeException("Could not visit element " + e, ex);
				}
			}
		}
		report(this.nonGeneratedMethods);
		return null;
	}

	private void report(Map<CompilationUnit, List<MethodDeclaration>> nonGeneratedMethods2) {
		final StringBuilder sb = new StringBuilder("Non generated methods found in given selected Java elements\n");
		for (final Entry<CompilationUnit, List<MethodDeclaration>> entry : nonGeneratedMethods2.entrySet()) {
			final List<MethodDeclaration> methodList = entry.getValue();
			if (!methodList.isEmpty()) {
				final CompilationUnit unit = entry.getKey();
				final IJavaElement javaElement = unit.getJavaElement();
				final String elementName = javaElement.getElementName();
				sb.append("  type >> " + elementName + " :\n");
				for (MethodDeclaration m : methodList) {
					sb.append("    ." + m.getName() + "()\n");
				}
			}
		}

		final MessageConsole findConsole = findConsole(COMMAND_ID);
		findConsole.activate();
		findConsole.clearConsole();
		MessageConsoleStream out = findConsole.newMessageStream();
		out.println(sb.toString());
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private void visit(IJavaElement e) throws JavaModelException {
		if (e instanceof ICompilationUnit) {
			process((ICompilationUnit) e);
		} else if (e instanceof IParent) {
			visit((IParent) e);
		} else {
			// do nothing
		}
	}

	private void visit(IParent e) throws JavaModelException {
		for (IJavaElement child : e.getChildren()) {
			visit(child);
		}
	}

	/**
	 * Parse the Java file, visit its AST and fill the list of non generated methods
	 */
	private void process(ICompilationUnit source) {
		final ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source);
		parser.setResolveBindings(true); // we need bindings later on
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		final NonGeneratedMethodAstVisitor visitor = new NonGeneratedMethodAstVisitor();
		unit.accept(visitor);
		this.nonGeneratedMethods.put(unit, visitor.getNonGeneratedMethods());

	}
}
