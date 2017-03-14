package de.tud.cs.peaks.importing.xml;

import de.tud.cs.peaks.misc.PeaksField;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.misc.Visibility;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.Path;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static de.tud.cs.peaks.importing.xml.SerializationConstants.*;
import static de.tud.cs.peaks.misc.Visibility.*;
import static de.tud.cs.peaks.results.AnalysisType.*;

public class ResultDeserializer extends AbstractResultDeserializer {

	private AnalysisResult result;

	public ResultDeserializer(String xml) throws ParsingException, IOException {
		super(xml);
	}

	public ResultDeserializer(File xml) throws IOException, ParsingException {
		super(xml);
	}

	public ResultDeserializer(InputStream xml) throws ParsingException,
			IOException {
		super(xml);
	}

	@Override
	protected void parseResult() {
		result = parseType();
		parseMethods();
		parseFields();
		parsePaths();
	}

	private void parsePaths() {
        Set<Path> pathList = new HashSet<>();
		Elements pathsElements = root.getChildElements(PATHS_NODE);
        for (int i = 0; i < pathsElements.size(); i++) {
            Elements paths = pathsElements.get(i).getChildElements(PATH_NODE);
            LinkedList<PeaksMethod> methodPath = new LinkedList<>();
            for (int j = 0; j < paths.size(); j++) {
                Element path = paths.get(i);
                Elements methods = path.getChildElements(METHOD_NODE);
                for (int k = 0; k < methods.size() ; k++) {
                    Element method = methods.get(k);
                    methodPath.add(deserializeMethod(method));
                }
            } 
            pathList.add(new Path(methodPath));
        }
        result.setPaths(pathList);
    }

	private AnalysisResult parseType() {
		String type = root.getLocalName();
		String version = root.getAttributeValue(ANALYSIS_VERSION_ATTRIBUTE);
		if (DIRECT_NATIVE_USAGE_RESULT.getDescription().equals(type)) {
			return new AnalysisResult(version, DIRECT_NATIVE_USAGE_RESULT);
		} else if (TRANSITIVE_NATIVE_USAGE_RESULT.getDescription().equals(type)) {
			return new AnalysisResult(version, TRANSITIVE_NATIVE_USAGE_RESULT);
		} else if (STATIC_USAGE_ANALYSIS_RESULT.getDescription().equals(type)) {
			return new AnalysisResult(version, STATIC_USAGE_ANALYSIS_RESULT);
		} else if (REFLECTION_ANALYSIS_RESULT.getDescription().equals(type)) {
			return new AnalysisResult(version, REFLECTION_ANALYSIS_RESULT);
		}
		if (logger.isErrorEnabled()) {
			logger.error("Could not parse result type");
		}
		throw new RuntimeException();
	}

	private void parseFields() {
		Elements fieldsNode = root.getChildElements(FIELDS_NODE);
		for (int i = 0; i < fieldsNode.size(); i++) {
			Elements fieldsByVisibility = fieldsNode.get(i).getChildElements();
			for (int j = 0; j < fieldsByVisibility.size(); j++) {
				Element visibilityNode = fieldsByVisibility.get(j);
				Elements currentFields = visibilityNode.getChildElements();
				for (int k = 0; k < currentFields.size(); k++) {
					Element field = currentFields.get(k);
					PeaksField peaksField = deserializeField(field);
					if (visibilityNode.getLocalName().equals(IMMUTABLE_NODE)) {
						result.addImmutableField(peaksField);
					} else {
						result.addField(peaksField);
					}
				}
			}
		}
	}

	private PeaksField deserializeField(Element field) {
		String name = field.getValue();
		String path = field.getAttribute(PACKAGE_ATTRIBUTE).getValue();
		String type = field.getAttribute(TYPE_ATTRIBUTE).getValue();
		Visibility visibility = getVisibility(field
				.getAttributeValue(VISIBILITY_ATTRIBUTE));
		float rating = findRating(field);
		return new PeaksField(name, visibility, path, type, rating);
	}

	private Visibility getVisibility(String visibility) {
		switch (visibility) {
		case PUBLIC_NODE:
			return Public;
		case PRIVATE_NODE:
			return Private;
		case PROTECTED_NODE:
			return Protected;
		case DEFAULT_NODE:
			return Default;
		default:
			throw new RuntimeException("Visibility not found");
		}
	}
    private PeaksMethod deserializeMethod(Element method){
        LinkedList<String> parameters = new LinkedList<>();
        Visibility visibility = getVisibility(method
                .getAttributeValue(VISIBILITY_ATTRIBUTE));
        String name = method.getValue();
        String returnValue = method
                .getAttributeValue(RETURN_VALUE_ATTRIBUTE);
        String path = method.getAttributeValue(PACKAGE_ATTRIBUTE);
        findParameters(method, parameters);
        float rating = findRating(method);
        return new PeaksMethod(name,
                returnValue, visibility, path, rating, parameters);
    }


	private void parseMethods() {
		Elements methodsNode = root.getChildElements(METHODS_NODE);
		for (int i = 0; i < methodsNode.size(); i++) {
			Elements methodsByVisibility = methodsNode.get(i)
					.getChildElements();
			for (int j = 0; j < methodsByVisibility.size(); j++) {
				Element visibilityNode = methodsByVisibility.get(j);
				Elements currentMethods = visibilityNode.getChildElements();
				for (int k = 0; k < currentMethods.size(); k++) {
					Element method = currentMethods.get(k);
                    PeaksMethod peaksMethod = deserializeMethod(method);
					if (visibilityNode.getLocalName().equals(
							SIDE_EFFECT_FREE_NODE)) {
						result.addSideEffectFreeMethod(peaksMethod);
					} else {
						result.addMethod(peaksMethod);
					}
				}
			}
		}
	}

	private float findRating(Element element) {
		Attribute rating = element.getAttribute(RATING_ATTRIBUTE);
		return (rating != null) ? Float.parseFloat(rating.getValue()) : 1.0f;
	}

	private void findParameters(Element method, List<String> parameters) {
		String params = method.getAttributeValue(PARAMS_ATTRIBUTE);
		if (!params.equals("[]")) {
			String[] split = params.substring(1, params.length() - 1)
					.split(",");
			Collections.addAll(parameters, split);
		}
	}

	public AnalysisResult getResult() {
		return result;
	}
}
