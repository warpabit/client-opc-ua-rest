package eu.arrowhead.client.provider;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

/**
 * This class contains different ways of interacting with OPC-UA. Note that the clients
 * supplied to the functions must already be connected (e.g. created through the OPCUAConnection class)
 * @author Niklas Karvonen
 */


public class OPCUAInteractions {

    public static Vector<String> browseNode(OpcUaClient client, NodeId browseRoot) {
        //String returnString = "";
        Vector<String> returnNodes = new Vector<String>();
         try {
            List<Node> nodes = client.getAddressSpace().browse(browseRoot).get();
            for(Node node:nodes) {
                returnNodes.add("ns=" + node.getNodeId().get().getNamespaceIndex() + ",identifier=" + node.getNodeId().get().getIdentifier() + ",displayName=" + node.getDisplayName().get().getText() + ",nodeClass=" + node.getNodeClass().get());
            }
        } catch (Exception e) {
            System.out.println("Browsing nodeId=" + browseRoot + " failed: " + e.getMessage());
        }
         return returnNodes;
    }


    public static String readVariableNode(OpcUaClient client, NodeId nodeId) {
        String returnString = "";
        try {
            VariableNode node = client.getAddressSpace().createVariableNode(nodeId);
            DataValue value = node.readValue().get();

            CompletableFuture<DataValue> test = client.readValue(0.0, TimestampsToReturn.Both, nodeId);
            DataValue data = test.get();
            System.out.println("DataValue Object: " + data);
            returnString = data.toString();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }
        return returnString;
    }

    public static String writeNode(OpcUaClient client, NodeId nodeId, int value) {
        String returnString = "";
        returnString += value;
        try {
            VariableNode node = client.getAddressSpace().createVariableNode(nodeId);
            Variant v = new Variant(new Integer(value));
            DataValue data = new DataValue(v,null, null);
            CompletableFuture<StatusCode> f = client.writeValue(nodeId, data);
            StatusCode status = f.get();
            System.out.println("Wrote DataValue: " + data + " status: " + status);
            returnString = status.toString();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }
        return returnString;
    }

}
