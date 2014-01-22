package builder;

public class EqNodeBuilder {
	
	public static void main(String[] args) {
		
		EqNode equation = new EqNode("3(x-1)y(z+2)_pow{2,3(x+3)}+2_pow{3,2}");
		EqNode root = equation.getRootNode();
		
		System.out.println(root);
		
	}
	
	
}
