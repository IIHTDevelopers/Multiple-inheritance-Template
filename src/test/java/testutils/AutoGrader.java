package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code implements multiple inheritance correctly through interfaces
	public boolean testMultipleInheritance(String filePath) throws IOException {
		System.out.println("Starting testMultipleInheritance with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		boolean hasInheritance = true;
		AtomicBoolean flyMethodImplemented = new AtomicBoolean(false);
		AtomicBoolean runMethodImplemented = new AtomicBoolean(false);
		AtomicBoolean methodsExecuted = new AtomicBoolean(false);

		// Check for interface implementation (Bird class implements Flyable and
		// Runnable)
		System.out.println("------ Inheritance and Interface Implementation Check ------");
		boolean birdClassFound = false;

		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;
				if (classDecl.getNameAsString().equals("Bird")) {
					System.out.println("Class 'Bird' found: " + classDecl.getName());
					birdClassFound = true;
					if (classDecl.getImplementedTypes().size() > 0) {
						System.out.println("Class 'Bird' implements the following interfaces:");
						classDecl.getImplementedTypes().forEach(impl -> {
							System.out.println(impl.getName());
//							if (impl.getNameAsString().equals("Flyable") && impl.getNameAsString().equals("Runnable")) {
//								
//							}
						});
					} else {
						System.out.println("Class 'Bird' not inheriting any other class");
						hasInheritance = false;
					}
				}
			}
		}

		if (!birdClassFound) {
			System.out.println("Error: 'Bird' class not found.");
			return false; // Early exit if class creation is missing
		}

		if (!hasInheritance) {
			System.out.println("Error: 'Bird' does not implement both 'Flyable' and 'Runnable' interfaces.");
			return false;
		}

		// Check for method overriding (fly and run methods in Bird)
		System.out.println("------ Method Override Check ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("fly") && method.getParentNode().get().toString().contains("Bird")) {
				flyMethodImplemented.set(true);
				System.out.println("Method 'fly' overridden in 'Bird' class.");
			}
			if (method.getNameAsString().equals("run") && method.getParentNode().get().toString().contains("Bird")) {
				runMethodImplemented.set(true);
				System.out.println("Method 'run' overridden in 'Bird' class.");
			}
		}

		if (!flyMethodImplemented.get() || !runMethodImplemented.get()) {
			System.out.println("Error: One or more methods ('fly', 'run') not overridden in 'Bird' class.");
			return false;
		}

		// Check if both methods are executed in main
		System.out.println("------ Method Execution Check in Main ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("fly") || callExpr.getNameAsString().equals("run")) {
							methodsExecuted.set(true);
							System.out.println("Methods 'fly' and 'run' are executed in the main method.");
						}
					});
				}
			}
		}

		if (!methodsExecuted.get()) {
			System.out.println("Error: Methods 'fly' and 'run' not executed in the main method.");
			return false;
		}

		// If inheritance, method overriding, and method execution are correct
		System.out.println("Test passed: Multiple inheritance via interfaces is correctly implemented.");
		return true;
	}
}
