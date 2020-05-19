package lt.LinasJu;

import lt.LinasJu.Entities.Edges.Lane;
import lt.LinasJu.Entities.Network;
import lt.LinasJu.Entities.TlLogics.Phase;
import lt.LinasJu.Entities.TlLogics.SignalStateEnum;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class XmlRepo {
    CreationRepo creationRepo = new CreationRepo();

    public Document readXml(String xmlFileName) {
        try {
            //creating a constructor of file class and parsing an XML file
            File file = new File(xmlFileName);
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveNetworkToXmlFiles(String workingDirectory, String filename, Network network) {
        String outputFileNameBase = workingDirectory + filename;

        saveNetworkEntitiesToXmlFile(outputFileNameBase + FilesSuffixesEnum.NODES.toString(), Collections.singletonList(network.getNodes()));
        saveNetworkEntitiesToXmlFile(outputFileNameBase + FilesSuffixesEnum.EDGES.toString(), Arrays.asList(network.getEdges(), network.getRoundabouts()));
        saveNetworkEntitiesToXmlFile(outputFileNameBase + FilesSuffixesEnum.TYPE_OF_EDGES.toString(), Collections.singletonList(network.getEdgeTypes()));
        saveNetworkEntitiesToXmlFile(outputFileNameBase + FilesSuffixesEnum.CONNECTIONS.toString(), Collections.singletonList(network.getConnections()));
        saveNetworkEntitiesToXmlFile(outputFileNameBase + FilesSuffixesEnum.TRAFFIC_LIGHT_LOGICS.toString(), Arrays.asList(network.getTrafficLightLogics(), network.getTrafficLightLogicsConnections()));
    }

    /**
     *
     * @param outputFileName Output file name that output will be generated to.
     * @param listOfNodeLists Network entites wrapped in List - in case it is needed to write more than one entity to one file. Array Order must be correct
     */
    private void saveNetworkEntitiesToXmlFile(String outputFileName, List<?> listOfNodeLists) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            String rootNodeElementName = StringUtils.getRootNodeElementNameFromClass(((List<?>) listOfNodeLists.get(0)).get(0).getClass());
            Document doc = docBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement(rootNodeElementName);
            listOfNodeLists.forEach(nodeList -> {
                //for every other nodeList instead of first, it is needed to set elementName
                setChildsToElement(doc, rootElement, (List<?>) nodeList);
                if (nodeList == listOfNodeLists.get(0)) {
                    doc.appendChild(rootElement);
                }
            });

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            creationRepo.createEmptyFile(outputFileName);
            StreamResult result = new StreamResult(new File(outputFileName));

            // Output to console for testing
//            StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    /**
     * This function if needed creates new additional Elements, adds passed List of values as new elements' child.
     * @param doc The main document that is being created.
     * @param rootElementToAddAttributes The element to which will be added attributes.
     * @param nodeList List of nodes to be added to element
     */
    private void setChildsToElement(Document doc, Element rootElementToAddAttributes, List<?> nodeList) {

        // node element
        // set attributes to node element
        nodeList.forEach(node -> {
            String nodeElementName = StringUtils.decapitalize(node.getClass().getSimpleName());
            Element nodeElement = doc.createElement(nodeElementName);
            rootElementToAddAttributes.appendChild(nodeElement);
            setAttributesToElement(doc, node, nodeElement);
        });
    }

    /**
     *
     * @param doc The main document that is being created.
     * @param node List or object of values to add as attributes to Element.
     * @param nodeElement the Element to add attributes to.
     */
    private void setAttributesToElement(Document doc, Object node, Element nodeElement) {
        Field[] fields = node.getClass().getDeclaredFields();
        Field[] superclassFields = node.getClass().getSuperclass().getDeclaredFields();

        setAttributesFromEntityFields(doc, node, nodeElement, fields);
        if (superclassFields.length != 0) {
            setAttributesFromEntityFields(doc, node, nodeElement, superclassFields);
        }
    }

    /**
     *
     * @param doc The main document that is being created.
     * @param object List or object of values to add as attributes to Element.
     * @param nodeElement the Element to add attributes to.
     * @param objectFields all object fields to set as Attributes to Element.
     */
    private void setAttributesFromEntityFields(Document doc, Object object, Element nodeElement, Field[] objectFields) {
        Arrays.stream(objectFields).forEach(field -> {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(object);

                if (fieldValue == null) {
                    return;
                }

                if (field.getType() == List.class) {
                    setListAttributeToElement(doc, nodeElement, field.getName(), (List<?>) fieldValue);
                    return;
                }

                if (field.isEnumConstant()) {
                    setEnumAttributeToElement(doc, nodeElement, field.getName(), fieldValue);
                    return;
                }

                setSimpleAttributeToElement(doc, nodeElement, field.getName(), String.valueOf(fieldValue));

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private void setSimpleAttributeToElement(Document doc, Element nodeElement, String name, String s) {
        Attr attr = doc.createAttribute(name);
        attr.setValue(s);
        nodeElement.setAttributeNode(attr);
    }

    private void setListAttributeToElement(Document doc, Element nodeElement, String elementName, List<?> fieldValue) {
        if (fieldValue.get(0).getClass() == Lane.class || fieldValue.get(0).getClass() == Phase.class) {
            setChildsToElement(doc, nodeElement, fieldValue);
            return;
        }
        setConcatenatedListValuesAttributeToElement(doc, nodeElement, elementName, fieldValue);
    }

    private void setEnumAttributeToElement(Document doc, Element nodeElement, String elementName, Object enumObject) {
        setConcatenatedListValuesAttributeToElement(doc, nodeElement, elementName, Collections.singletonList(enumObject));
    }

    /**
     *  @param doc The main document that is being created.
     * @param nodeElement The Element to add attributes to.
     * @param elementName element name to add attribute to.
     * @param objectList List of enums to create attribute value from.
     */
    private void setConcatenatedListValuesAttributeToElement(Document doc, Element nodeElement, String elementName, List<?> objectList) {
        //for signalStateEnums is a must to be joined without spaces, for others is a must to be joined with spaces.
        StringJoiner joiner = new StringJoiner(objectList.get(0).getClass() == SignalStateEnum.class ? "" : " ");
        objectList.stream().map(Object::toString).forEach(joiner::add);
        setSimpleAttributeToElement(doc, nodeElement, elementName, joiner.toString());
    }

}
