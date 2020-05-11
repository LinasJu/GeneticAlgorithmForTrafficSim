package lt.LinasJu;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.*;

public class ParserRepo {
    /**
     *
     * @param doc a document to be parsed. must be data with tags from xml file.
     * @return nodes map with all their attributes and additional lower level attributes
     */
    public Map<String, List<Map<String, Object>>> parseDocumentToObjects(Document doc) {
        doc.getDocumentElement().normalize();
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        Node node = doc.getDocumentElement().getFirstChild();

        return parseAllNodes(node);
    }

    private Map<String, Object> getNodeAttributes(NamedNodeMap mapAttributes) {
        Map<String, Object> nodeMap = new HashMap<>();
        for (int i = 0; i < mapAttributes.getLength(); i++) {
            Node node = mapAttributes.item(i);
            nodeMap.put(node.getNodeName(), node.getNodeValue());
        }

        return nodeMap;
    }

    /**
     * recursive function to find all node parameters including lower level sub-nodes
     * @param node the node that will be checked for attributes and children sub-nodes
     * @return all parsed nodes, sorted by node type
     */
    private Map<String, List<Map<String, Object>>> parseAllNodes(Node node) {
        Map<String, List<Map<String, Object>>> nodes = new HashMap<>();
        while (node != null) {
            if (node.hasAttributes()) {
                Map<String, Object> allNodeAttributes = getNodeAttributes(node.getAttributes());

                if (node.getFirstChild() != null) { //checks for sub-nodes
                    Node subNode = node.getFirstChild().getNextSibling();
                    allNodeAttributes.put(subNode.getNodeName(), parseAllNodes(subNode).get(subNode.getNodeName()));
                }

                getNodesListByNodeName(nodes, node.getNodeName()).add(allNodeAttributes);
            }
            node = node.getNextSibling();
        }
        return nodes;
    }

    private List<Map<String, Object>> getNodesListByNodeName(Map<String, List<Map<String, Object>>> nodes, String nodeKey) {
        if (nodes.get(nodeKey) != null) {
            return nodes.get(nodeKey);
        }
        nodes.put(nodeKey, new ArrayList<>());
        return nodes.get(nodeKey);
    }
}
