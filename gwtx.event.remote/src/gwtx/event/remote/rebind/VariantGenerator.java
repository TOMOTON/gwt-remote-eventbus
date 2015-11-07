/**
 * Licensed to TOMOTON nv under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  TOMOTON nv licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gwtx.event.remote.rebind;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.GeneratedResource;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates a signature at compile time. This version is derived
 * at using a random UUID.
 * 
 * @author Dann Martens
 */
public class VariantGenerator extends Generator {

	private static final String PATH = "variant/";

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		String result = null;
		try {
			String variantValue = UUID.randomUUID().toString();
			JClassType classType = context.getTypeOracle().getType(typeName);				
			String packageName = packageNameFrom(classType);
			String simpleName = simpleNameFrom(classType);			
			result = packageName + '.' + simpleName;				
			SourceWriter source = getSourceWriter(logger, context, classType);
			if(source != null) { //? Otherwise, work needs to be done.
				source.println();
				source.println("private String value;");
				source.println();
				source.println("public " + simpleName + "() {");
				populateInstanceFactory(logger, context, typeName, source, variantValue);
				source.println("}");
				source.println();
				source.println("@Override");
				source.println("public String getValue() {");
				source.println("  return value;");
				source.println("}");
				source.println();
				source.println("@Override");
				source.println("public String getPath() {");
				source.println("  return \"" + PATH + "\";");
				source.println("}");
				source.println();
				source.commit(logger);			
				emitSignatureArtifact(logger, context, variantValue);
			}
		} catch (NotFoundException nfe) {
			logger.log(Type.ERROR, "Could not find type '" + typeName + "'!", nfe);
			throw new UnableToCompleteException();
		}
		return result;
	}

	private void populateInstanceFactory(TreeLogger logger, GeneratorContext context, String typeName, SourceWriter source, String variantValue) throws UnableToCompleteException {
		source.println("  this.value =\""+ variantValue +"\";");
	}

	private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext context, JClassType classType) {
		String packageName = packageNameFrom(classType);
		String simpleName = simpleNameFrom(classType);
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
		composer.addImplementedInterface(classType.getName());
		composer.addImport(classType.getQualifiedSourceName());
		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter == null) {
			return null;
		} else {
			return composer.createSourceWriter(context, printWriter);
		}
	}

	private String packageNameFrom(JClassType classType) {
		return classType.getPackage().getName();
	}

	private String simpleNameFrom(JClassType classType) {
		return classType.getSimpleSourceName() + "_default_VariantGenerator";	
	}
	
	private void emitSignatureArtifact(TreeLogger logger, GeneratorContext context, String variantValue) throws UnableToCompleteException {
		try {
			OutputStream out = context.tryCreateResource(logger, PATH + variantValue);
			out.write(" ".getBytes("UTF-8"));
			GeneratedResource resource = context.commitResource(logger, out);
			resource.setVisibility(Visibility.Public);
		} catch (UnsupportedEncodingException uee) {
			logger.log(TreeLogger.ERROR, "UTF-8 is not supported", uee);
			throw new UnableToCompleteException();
		} catch (IOException e) {
			logger.log(TreeLogger.ERROR, null, e);
			throw new UnableToCompleteException();
		}
	}

}
