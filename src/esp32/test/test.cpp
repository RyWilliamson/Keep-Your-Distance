#include <Arduino.h>
#include <AUnitVerbose.h>
#include "rbtree.h"
#include "main.h"
#include "common.h"

class TreeTests: public aunit::TestOnce {
    protected:
        AverageRBTree *tree;
        void setup() override {
            aunit::TestOnce::setup();
            tree = new AverageRBTree();
        }

        void teardown() override {
            delete tree;
            aunit::TestOnce::teardown();
        }

        void assertTreeSize(int size) {
            assertEqual(tree->getSize(), size);
        }

        pNode constructNode(uint64_t mac, float rssi, uint32_t timestamp, bool isRed) {
            pNode node = new Node;
            node->mac = mac;
            node->rssi = rssi;
            node->timestamp = timestamp;
            node->parent = nullptr;
            node->left = nullptr;
            node->right = nullptr;
            node->isRed = isRed;
            return node;
        }

        void generateRandomTree(int nodes) {
            for (int i = 0; i < nodes; i++) {
                tree->insertNode(random(50), 0, 0);
            }
        }
};

/* Need to manually construct tree as not yet tested insert */
testF(TreeTests, inOrderString) {
    pNode root = constructNode(1, 0, 0, false);
    pNode leftChild = constructNode(0, 0, 0, true);
    pNode rightChild = constructNode(2, 0, 0, true);

    root->left = leftChild;
    root->right = rightChild;

    leftChild->parent = root;
    leftChild->left = tree->getLeafTest();
    leftChild->right = tree->getLeafTest();

    rightChild->parent = root;
    rightChild->left = tree->getLeafTest();
    rightChild->right = tree->getLeafTest();

    tree->setRootTest(root);

    std::ostringstream os;
    os << 0 << " " << 0 << " " << 0 << std::endl;
    os << 1 << " " << 0 << " " << 0 << std::endl;
    os << 2 << " " << 0 << " " << 0 << std::endl;
    std::string out = tree->inOrderString();
    assertStringCaseEqual(out.c_str(), os.str().c_str());
}

/* Tests that insert properly adds to the tree and updates size for a single node */
testF(TreeTests, firstInsert) {
    tree->insertNode(0, 0, 0);
    assertTreeSize(1);
}

/* Test that clear works */
testF(TreeTests, clear) {
    tree->insertNode(0, 0, 0);
    tree->clearTree();
    assertTreeSize(0);
}

/* Tests that insert adds to tree and updates size for multiple nodes */
testF(TreeTests, multiInsert) {
    tree->insertNode(0, 0, 0);
    tree->insertNode(1, 0, 0);
    tree->insertNode(5, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(4, 0, 0);
    tree->insertNode(2, 0, 0);
    assertTreeSize(6);
    assertTrue(true); // Needed for multiple assertion rules within library.
    assertStringCaseEqual(tree->inOrderString().c_str(), "0 0 0\n1 0 0\n2 0 0\n3 0 0\n4 0 0\n5 0 0\n");
}

/* Tests that insert properly changes the root node */
testF(TreeTests, rootCheck) {
    tree->insertNode(0, 0, 0);
    tree->insertNode(1, 0, 0);
    tree->insertNode(2, 0, 0);
    assertEqual(tree->getRootTest()->mac, (uint64_t) 1);
}

/* Tests that min does return the min value from tree */
testF(TreeTests, treeMin) {
    tree->insertNode(4, 0, 0);
    tree->insertNode(5, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(0, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(1, 0, 0);
    assertEqual(tree->min(tree->getRootTest())->mac, (uint64_t) 0);
}

/* Tests that max does return the min value from tree */
testF(TreeTests, treeMax) {
    tree->insertNode(4, 0, 0);
    tree->insertNode(5, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(0, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(1, 0, 0);
    assertEqual(tree->max(tree->getRootTest())->mac, (uint64_t) 5);
}

/* Tests delete removes a red node and decrements the size */
testF(TreeTests, deleteRed) {
    tree->insertNode(4, 0, 0);
    tree->insertNode(5, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(0, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(1, 0, 0);
    tree->deleteNode(2);
    assertTreeSize(5);
    assertTrue(true); // Needed for multiple assertion rules within library.
    assertStringCaseEqual(tree->inOrderString().c_str(), "0 0 0\n1 0 0\n3 0 0\n4 0 0\n5 0 0\n");
}

/* Tests delete removes a black node and decrements the size */
testF(TreeTests, deleteBlack) {
    tree->insertNode(4, 0, 0);
    tree->insertNode(5, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(0, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(1, 0, 0);
    tree->deleteNode(0);
    assertTreeSize(5);
    assertTrue(true); // Needed for multiple assertion rules within library.
    assertStringCaseEqual(tree->inOrderString().c_str(), "1 0 0\n2 0 0\n3 0 0\n4 0 0\n5 0 0\n");
}

/* Tests delete removes the root and decrements the size */
testF(TreeTests, deleteRoot) {
    tree->insertNode(4, 0, 0);
    tree->insertNode(5, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(0, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(1, 0, 0);
    tree->deleteNode(4);
    assertTreeSize(5);
    assertTrue(true); // Needed for multiple assertion rules within library.
    assertStringCaseEqual(tree->inOrderString().c_str(), "0 0 0\n1 0 0\n2 0 0\n3 0 0\n5 0 0\n");
}

/* Tests that delete on an invalid node doesn't affect the tree */
testF(TreeTests, deleteNone) {
    tree->insertNode(4, 0, 0);
    tree->insertNode(5, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(0, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(1, 0, 0);
    tree->deleteNode(6);
    assertTreeSize(6);
    assertTrue(true); // Needed for multiple assertion rules within library.
    assertStringCaseEqual(tree->inOrderString().c_str(), "0 0 0\n1 0 0\n2 0 0\n3 0 0\n4 0 0\n5 0 0\n");
}

// class CommonTests: public aunit::TestOnce {
//     protected:
//         void setup() override {
//             aunit::TestOnce::setup();
//         }

//         void teardown() override {
//             aunit::TestOnce::teardown();
//         }
// };

class MainTests: public aunit::TestOnce {
    protected:
        void setup() override {
            aunit::TestOnce::setup();
        }

        void teardown() override {
            aunit::TestOnce::teardown();
        }
};

// testF(MainTests, calculateDistance) {
//     // assertNear(calculateDistance(-81, -81, 3), 1.0, 0);
//     // assertEqual(calculateDistance((short)-81, -81.0, 3.0), 1.0);
//     setupTile();
//     assertTrue(true);
// }

void setup() {
    Serial.begin(115200);
}

void loop() {
    aunit::TestRunner::run();
}