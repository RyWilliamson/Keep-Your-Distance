// Adapted from https://github.com/Bibeknam/algorithmtutorprograms/blob/master/data-structures/red-black-trees/RedBlackTree.cpp
#ifndef RBTREE
#define RBTREE

#include <Arduino.h>
#include <string>

struct Node {
    uint64_t mac;
    float rssi;
    uint32_t timestamp;

    Node *parent;
    Node *left;
    Node *right;

    bool isRed;
};

typedef Node *pNode;

class AverageRBTree {
private:
    pNode root;
    pNode leaf;

    // Creates empty (default) node
    void createLeafNode(pNode node, pNode parent) {
        node->mac = 0;
        node->rssi = 0;
        node->timestamp = 0;

        node->parent = parent;
        node->left = nullptr;
        node->right = nullptr;
        node->isRed = false;
    }

    void inOrderHelper(pNode node) {
        std::string mac_string = "";
        if (node != leaf) {
            inOrderHelper(node->left);
            Serial.printf("%llx %f %u\r\n", node->mac, node->rssi, node->timestamp);
            inOrderHelper(node->right);
        }
    }

    // Finds a node in the tree or returns a leaf node if not found
    pNode searchHelper(pNode node, uint64_t mac) {
        // Returns if we've found the correct node or reached a leaf
        if (node == leaf || mac == node->mac) {
            return node;
        }

        // Recursively search through the tree taking either left or right branch
        if (mac < node->mac) {
            return searchHelper(node->left, mac);
        } else {
            return searchHelper(node->left, mac);
        }
    }

    // Fixes the tree when modified by delete
    void correctDeletion(pNode node) {
        pNode sibling;
        // Case 3
        while (node != root && !node->isRed) {
            // Check if node is left or right child
            if (node == node->parent->left) {
                // Node is left child
                sibling = node->parent->right;

                if (sibling->isRed) {
                    // Case 3.1 - Sibling is red
                    sibling->isRed = false;
                    node->parent->isRed = true;
                    leftRotation(node->parent);
                    sibling = node->parent->right;
                }

                if (!sibling->left->isRed && !sibling->right->isRed) {
                    // Case 3.2 - Sibling is black, and both sibling child are black
                    sibling->isRed = true;
                    node = node->parent;
                } else {

                    if (!sibling->right->isRed) {
                        // Case 3.3 - Sibling is black, sibling left child red, right child black
                        sibling->left->isRed = false;
                        sibling->isRed = true;
                        rightRotation(sibling);
                        sibling = node->parent->right;
                    }

                    // Case 3.4 Sibling is black, sibling right child is red (don't care about left child)
                    sibling->isRed = node->parent->isRed;
                    node->parent->isRed = false;
                    sibling->right->isRed = false;
                    leftRotation(node->parent);
                    node = root;
                }
            } else {
                // Node is right child
                sibling = node->parent->left;

                if (sibling->isRed) {
                    // Case 3.1 - Sibling is red
                    sibling->isRed = false;
                    node->parent->isRed = true;
                    rightRotation(node->parent);
                    sibling = node->parent->left;
                }

                if (!sibling->left->isRed && !sibling->right->isRed) {
                    // Case 3.2 - Sibling is black, and both sibling child are black
                    sibling->isRed = true;
                    node = node->parent;
                } else {

                    if (!sibling->left->isRed) {
                        // Case 3.3 - Sibling is black, sibling left child black, right child red
                        sibling->right->isRed = false;
                        sibling->isRed = true;
                        leftRotation(sibling);
                        sibling = node->parent->left;
                    }

                    // Case 3.4 Sibling is black, sibling left child is red (don't care about right child)
                    sibling->isRed = node->parent->isRed;
                    node->parent->isRed = false;
                    sibling->left->isRed = false;
                    rightRotation(node->parent);
                    node = root;
                }
            }
        }
        node->isRed = false;
    }

    // Used for replacing node.
    void transplant(pNode old_node, pNode new_node) {
        if (old_node->parent == nullptr) {
            // n1 is root so just change root
            root = new_node;
        } else if (old_node == old_node->parent->left) {
            // n1 is left child so change left child
            old_node->parent->left = new_node;
        } else {
            // n1 is right child so change right child
            old_node->parent->right = new_node;
        }
        // Finally set parent
        new_node->parent = old_node->parent;
    }

    // Deletes node with mac address mac from tree
    void deletionHelper(pNode node, uint64_t mac) {
        // Find the node containing the mac address
        pNode target = leaf;
        pNode x, y;

        // Search until we exhaust all options
        while (node != leaf) {
            if (node->mac == mac) {
                // Found it
                target = node;
            }
            
            // Search relevant branch
            if (node->mac <= mac) {
                node = node->right;
            } else {
                node = node->left;
            }
        }

        if (target == leaf) {
            Serial.println("Nothing to delete from tree");
            return;
        }

        y = target;
        bool y_original_is_red = y->isRed;
        if (target->left == leaf) {
            // Since left child is leaf we can ignore and replace target with right child
            x = target->right;
            transplant(target, target->right);
        } else if (target->right == leaf) {
            // Since right child is leaf we can ignore and replace target with left child
            x = target->left;
            transplant(target, target->left);
        } else {
            // Since there are nodes on both children, need to move both subtrees
            y = min(target->right);
            y_original_is_red = y->isRed;
            x = y->right;

            if (y->parent == target) {
                x->parent = y;
            } else {
                transplant(y, y->right);
                y->right = target->right;
                y->right->parent = y;
            }

            transplant(target, y);
            y->left = target->left;
            y->left->parent = y;
            y->isRed = target->isRed;
        }
        delete target;
        if (!y_original_is_red) {
            correctDeletion(x);
        }
    }
    void correctInsertion(pNode node) {
        pNode uncle;

        while (node->parent->isRed) {
            
            if (node->parent == node->parent->parent->right) {
                // If parent is grandparents right child
                uncle = node->parent->parent->left;
                
                if (uncle->isRed) {
                    // Case 3.1 - Parent and Uncle are red
                    uncle->isRed = false;
                    node->parent->isRed = false;
                    node->parent->parent->isRed = true;
                    node = node->parent->parent;
                } else {
                    // Case 3.2 - Parent is red and Uncle is black
                    if (node == node->parent->left) {
                        // Case 3.2.2 Parent is grandparents right child and node is left child
                        node = node->parent;
                        rightRotation(node);
                    }
                    // Case 3.2.1 Parent is grandparents right child and node is right child
                    node->parent->isRed = false;
                    node->parent->parent->isRed = true;
                    leftRotation(node->parent->parent);
                }

            } else {
                // If parent is gradparents left child
                uncle = node->parent->parent->right;

                if (uncle->isRed) {
                    // Case 3.1 - Parent and Uncle are red
                    uncle->isRed = false;
                    node->parent->isRed = false;
                    node->parent->parent->isRed = true;
                    node = node->parent->parent;
                } else {
                    // Case 3.2 - Parent is red and Uncle is black
                    if (node == node->parent->right) {
                        // 3.2.4 Parent is grandparents left child and node is right child
                        node = node->parent;
                        leftRotation(node);
                    }
                    // Case 3.2.3 Parent is grandparents left child and node is left child
                    node->parent->isRed = false;
                    node->parent->parent->isRed = true;
                    rightRotation(node->parent->parent);
                }
            }
            if (node == root) {
                // Moved through the whole tree
                break;
            }
        }
        root->isRed = false;
    }

    void inOrderClear(pNode node, uint32_t time) {
        if (node != leaf) {
            inOrderHelper(node->left);
            
            if (time - node->timestamp > 7000) {
                deletionHelper(node, node->mac);
            }

            inOrderHelper(node->right);
        }
    }

public:
    AverageRBTree() {
        leaf = new Node();
        leaf->isRed = false;
        leaf->left = nullptr;
        leaf->right = nullptr;
        root = leaf;
    }

    pNode getLeaf() {
        return this->leaf;
    }

    void inOrder() {
        inOrderHelper(this->root);
    }

    pNode search(uint64_t mac) {
        return searchHelper(this->root, mac);
    }

    pNode min(pNode node) {
        while (node->left != leaf) {
            node = node->left;
        }
        return node;
    }

    pNode max(pNode node) {
        while (node->right != leaf) {
            node = node->right;
        }
        return node;
    }

    // Find nodes successor
    pNode successor(pNode node) {
        if (node->right != leaf) {
            // if right subtree isn't null, successor is leftmost node in right subtree
            return min(node->right);
        } else {
            // else it is the lowest ancestor of node whose left child is also an ancestor of node
            pNode y = node->parent;
            while (y != leaf && node == y->right) {
                node = y;
                y = y->parent;
            }
            return y;
        }
    }

    // Find nodes predecessor
    pNode predecessor(pNode node) {
        if (node->left != leaf) {
            // If left subtree isn't null, predecessor is rightmost node in left subtree
            return max(node->left);
        } else {
            // else it is the lowest ancestor of node whose right child is also an ancestor of node
            pNode y = node->parent;
            while (y != leaf && node == y->left) {
                node = y;
                y = y->parent;
            }
            return y;
        }
    }

    // Rotate left at node
    void leftRotation(pNode node) {
        pNode y = node->right;
        node->right = y->left;

        if (y->left != leaf) {
            y->left->parent = node;
        }

        y->parent = node->parent;
        if (node->parent == nullptr) {
            this->root = y;
        } else if (node == node->parent->left) {
            node->parent->left = y;
        } else {
            node->parent->right = y;
        }

        y->left = node;
        node->parent = y;
    }

    // Rotate right at node
    void rightRotation(pNode node) {
        pNode y = node->left;
        node->left = y->right;

        if (y->right != leaf) {
            y->right->parent = node;
        }

        y->parent = node->parent;
        if (node->parent == nullptr) {
            this->root = y;
        } else if (node == node->parent->right) {
            node->parent->right = y;
        } else {
            node->parent->left = y;
        }

        y->right = node;
        node->parent = y;
    }

    // Insert into the tree
    pNode insertNode(uint64_t mac, float rssi, uint32_t timestamp) {
        // Binary Search Tree Insertion
        pNode node = new Node;
        node->parent = nullptr;
        node->left = leaf;
        node->right = leaf;
        node->isRed = true;
        node->mac = mac;
        node->rssi = rssi;
        node->timestamp = timestamp;

        pNode y = nullptr;
        pNode x = this->root;

        while (x != leaf) {
            y = x;
            if (node->mac < x->mac) {
                x = x->left;
            } else {
                x = x->right;
            }
        }

        // y is parent of x
        node->parent = y;
        if (y == nullptr) {
            root = node;
        } else if (node->mac < y->mac) {
            y->left = node;
        } else {
            y->right = node;
        }

        // if new node is a root node, return
        if (node->parent == nullptr) {
            node->isRed = false;
            return node;
        }

        // if grandparent is null, return
        if (node->parent->parent == nullptr) {
            return node;
        }

        correctInsertion(node);
        return node;
    }

    void deleteNode(uint64_t mac) {
        deletionHelper(this->root, mac);
    }

    void cleanTree(uint32_t time) {
        inOrderClear(this->root, time);
    }
};

#endif