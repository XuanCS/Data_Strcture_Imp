//package edu.iastate.cs228.hw4;

/**
 * Node class which is used by the BinarySearchTree class to store data.
 * Normally this class would be a private class inside the BST class, it is
 * public for testing purposes. Because of this, most methods and constructors
 * have been implemented. You may add new protected methods, but DO NOT modify
 * already implemented or change their behavior or you risk losing points in
 * tests.
 * 
 * @author Xuan Lu
 */
public class Node<T> {

	/**
	 * Instance variables of the Nodes which are connected to this Node and the
	 * data it contains.
	 */
	private Node<T> left, right, parent;
	private T data;

	/**
	 * Creates a new Node that is disconnected from all others which stores the
	 * given data.
	 * 
	 * @param d
	 *            the node to be set
	 */
	public Node(T d) {
		this(null, null, null, d);
	}

	/**
	 * Creates a new Node that is connected to all the given Nodes and stores
	 * the given data.
	 * 
	 * @param d
	 *            the node to be set
	 */
	public Node(Node<T> left, Node<T> right, Node<T> parent, T d) {
		this.data = d;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

	/**
	 * Return current node's left child
	 * 
	 * @return the left child node of current node
	 */
	public Node<T> getLeft() {
		return this.left;
	}

	/**
	 * Set the left child node of current node
	 * 
	 * @param newLeft
	 *            the node to be set
	 */
	public void setLeft(Node<T> newLeft) {
		this.left = newLeft;
	}

	/**
	 * Return current node's right child
	 * 
	 * @return the right child node of current node
	 */
	public Node<T> getRight() {
		return this.right;
	}

	/**
	 * Set the right child node of current node
	 * 
	 * @param newRight
	 *            the node to be set
	 */
	public void setRight(Node<T> newRight) {
		this.right = newRight;
	}

	/**
	 * Get the current parent node
	 * 
	 * @return the parent of current node
	 */

	public Node<T> getParent() {
		return this.parent;
	}

	/**
	 * Set the right child node of current node
	 * 
	 * @param newRight
	 *            the node to be set
	 */
	public void setParent(Node<T> newParent) {
		this.parent = newParent;
	}

	/**
	 * Retrieve the data in type T
	 * 
	 * @return the data keep in T
	 */
	public T getData() {
		return this.data;
	}

	/**
	 * Set the new data to T
	 * 
	 * @param newData
	 *            the data to be set
	 */
	public void setData(T newData) {
		this.data = newData;
	}

	/**
	 * Returns the next Node in an inorder traversal of the BST which contains
	 * this Node.
	 * 
	 * @return the next Node in the traversal.
	 */
	public Node<T> getSuccessor() {
		if (this.right != null) {
			Node<T> next = this.right;
			while (next.left != null) {
				next = next.left;
			}
			return next;
		}
		Node<T> p = this.parent;
		Node<T> c = this;
		while (p != null && p.right == c) {
			c = p;
			p = p.parent;
		}
		return p;
	}

	/**
	 * Returns the previous Node in an inorder traversal of the BST which
	 * contains this Node.
	 * 
	 * @return the previous Node in the traversal.
	 */
	public Node<T> getPredecessor() {
		if (this.left != null) {
			Node<T> next = this.left;
			while (next.right != null) {
				next = next.right;
			}
			return next;
		}
		Node<T> p = this.parent;
		Node<T> c = this;
		while (p != null && p.left == c) {
			c = p;
			p = p.parent;
		}
		return p;
	}
}
