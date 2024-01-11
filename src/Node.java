public class Node {
    
    public static void main(String[] args) {
        if(args.length!=1){
            System.out.println("This node should receive only the node-type argument.");
            return;
        }
        try {
            NodeType nodeType = NodeType.valueOf(args[0].toUpperCase());
            NodeInstance node = new NodeInstance(nodeType);
            node.start();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid node type. Please choose one of: TYPE_A, TYPE_B, TYPE_C, TYPE_D");
        }
    }
}
