#include <Arduino.h>
#include <AUnitVerbose.h>
#include "rbtree.h"
#include "common.h"
#include "circularqueue.h"
#include "configdata.h"

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
    leftChild->left = tree->getLeaf();
    leftChild->right = tree->getLeaf();

    rightChild->parent = root;
    rightChild->left = tree->getLeaf();
    rightChild->right = tree->getLeaf();

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

/* Tests that search can properly find a node on left path */
testF(TreeTests, searchLeft) {
    tree->insertNode(5, 0, 0);
    tree->insertNode(6, 0, 0);
    tree->insertNode(4, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(7, 0, 0);
    pNode found = tree->search(2);
    assertEqual(found->mac, (uint64_t) 2);
}

/* Tests that search can properly find a node on right path */
testF(TreeTests, searchRight) {
    tree->insertNode(5, 0, 0);
    tree->insertNode(6, 0, 0);
    tree->insertNode(4, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(7, 0, 0);
    pNode found = tree->search(7);
    assertEqual(found->mac, (uint64_t) 7);
}

/* Tests that search can properly find a node down a branching path */
testF(TreeTests, searchBranch) {
    tree->insertNode(5, 0, 0);
    tree->insertNode(6, 0, 0);
    tree->insertNode(4, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(7, 0, 0);
    pNode found = tree->search(4);
    assertEqual(found->mac, (uint64_t) 4);
}

/* Tests that search returns leaf on not finding a node */
testF(TreeTests, searchNone) {
    tree->insertNode(5, 0, 0);
    tree->insertNode(6, 0, 0);
    tree->insertNode(4, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(3, 0, 0);
    tree->insertNode(7, 0, 0);
    pNode found = tree->search(99);
    assertEqual(found, tree->getLeaf());
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

class LogTests: public aunit::TestOnce {
    protected:
        CircularQueueLog *log;
        void setup() override {
            aunit::TestOnce::setup();
            log = new CircularQueueLog();
        }

        void teardown() override {
            delete log;
            aunit::TestOnce::teardown();
        }
};

/* Tests that log size is zero before any operations */
testF(LogTests, sizeInitiallyZero) {
    assertEqual(log->logLength(), 0);
}

/* Tests that the indexes get updated when inserting for the first time */
testF(LogTests, firstInsert) {
    uint8_t dummy_packet[17] = {};
    log->addToLog(dummy_packet);
    assertEqual(log->logLength(), 1);
}

/* Tests that the indexes get updated when inserting multiple times */
testF(LogTests, multipleInsert) {
    uint8_t dummy_packet[17] = {};
    log->addToLog(dummy_packet);
    log->addToLog(dummy_packet);
    log->addToLog(dummy_packet);
    log->addToLog(dummy_packet);
    assertEqual(log->logLength(), 4);
}

/* Tests that the indexes stop at the MAXLOG length */
testF(LogTests, fullInsert) {
    uint8_t dummy_packet[17] = {};
    for (int i = 0; i < MAXLOG + 1; i++) {
        log->addToLog(dummy_packet);
    }
    assertEqual(log->logLength(), MAXLOG);
}

/* Tests that the indexes get updated when inserting should wrap around */
testF(LogTests, wrapInsert) {
    uint8_t dummy_packet[17] = {};
    for (int i = 0; i < MAXLOG; i++) {
        log->addToLog(dummy_packet);
    }
    log->popFromLog(dummy_packet);
    log->popFromLog(dummy_packet);
    log->addToLog(dummy_packet);
    assertEqual(log->getFrontIndex(), 2);
    assertEqual(log->getRearIndex(), 0);
}

/* Tests that when removing from empty no change and flag set */
testF(LogTests, emptyRemove) {
    uint8_t dummy_packet[17] = {};
    log->popFromLog(dummy_packet);
    assertTrue(log->wasLogEmpty());
    assertEqual(log->logLength(), 0);
}

/* Tests that removing a single element doesn't set flag */
testF(LogTests, singleRemove) {
    uint8_t dummy_packet[17] = {};
    log->addToLog(dummy_packet);
    log->popFromLog(dummy_packet);
    assertFalse(log->wasLogEmpty());
    assertEqual(log->logLength(), 0);
}

/* Tests that size changes and no flag set on multiple add then remove */
testF(LogTests, multiRemove) {
    uint8_t dummy_packet[17] = {};
    log->addToLog(dummy_packet);
    log->addToLog(dummy_packet);
    log->addToLog(dummy_packet);
    log->popFromLog(dummy_packet);
    log->popFromLog(dummy_packet);
    assertFalse(log->wasLogEmpty());
    assertEqual(log->logLength(), 1);
}

/* Tests that removing works after a wrap around has occurred */
testF(LogTests, wrapRemove) {
    uint8_t dummy_packet[17] = {};
    for (int i = 0; i < MAXLOG; i++) {
        log->addToLog(dummy_packet);
    }

    log->popFromLog(dummy_packet);
    log->popFromLog(dummy_packet);
    log->popFromLog(dummy_packet);

    log->addToLog(dummy_packet);
    log->addToLog(dummy_packet);
    log->addToLog(dummy_packet);

    for (int i = 0; i < MAXLOG - 2; i++) {
        log->popFromLog(dummy_packet);
    }

    assertEqual(log->getFrontIndex(), 1);
    assertEqual(log->getRearIndex(), 1);
}

class ConfigTests: public aunit::TestOnce {
    protected:
        ConfigData *config;
        void setup() override {
            aunit::TestOnce::setup();
            config = new ConfigData(true);
        }

        void teardown() override {
            delete config;
            aunit::TestOnce::teardown();
        }
};

/* Tests that the inital state of the class is as expected */
testF(ConfigTests, checkInitial) {
    assertEqual(config->getMeasuredPower(), -81);
    assertEqual(config->getEnvironment(), 3);
    assertEqual(config->getDistance(), 1.5);
    assertEqual(config->getTargetRSSI(), -86);
}

/* Tests that the state of the class after an update is correct */
testF(ConfigTests, checkUpdate) {
    uint8_t bytes[12] = {0x40, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xAF, 0x00, 0x00, 0x00, 0x02};
    config->updateData(bytes);
    assertEqual(config->getMeasuredPower(), -81);
    assertEqual(config->getEnvironment(), 2);
    assertEqual(config->getDistance(), 2.0);
    assertEqual(config->getTargetRSSI(), -87);
}


void setup() {
    Serial.begin(115200);
}

void loop() {
    aunit::TestRunner::run();
}