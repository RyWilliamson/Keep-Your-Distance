#include <Arduino.h>
#include <AUnitVerbose.h>
#include "rbtree.h"
#include "common.h"
#include "circularqueue.h"
#include "configdata.h"
#include "rssihandler.h"

class TreeTests: public aunit::TestOnce {
    protected:
        AverageRBTree *tree;
        void setup() override {
            aunit::TestOnce::setup();
            tree = new AverageRBTree(true);
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
    assertEqual(config->getMeasuredPower(), -78);
    assertEqual(config->getEnvironment(), 2);
    assertEqual(config->getDistance(), 1.5);
    assertEqual(config->getTargetRSSI(), -81);
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

class RSSITests: public aunit::TestOnce {
    protected:
        RSSIHandler *handler;
        CircularQueueLog *log;
        AverageRBTree *tree;
        BLECharacteristic *rssiChar; // Dummy - will be ignored by test guards in functions
        BLECharacteristic *bulkChar; // Dummy - will be ignored by test guards in functions

        void setup() override {
            aunit::TestOnce::setup();
            log = new CircularQueueLog();
            tree = new AverageRBTree(true);
            handler = new RSSIHandler(log, true);

            rssiChar = new BLECharacteristic("80ba50ee-e010-4d75-b568-99de8adb10e4");
            bulkChar = new BLECharacteristic("80ba50ee-e010-4d75-b568-99de8adb10e5");
        }

        void teardown() override {
            delete handler;
            delete tree;
            delete log;
            aunit::TestOnce::teardown();
        }
};

/* Tests if the byte conversion to int64 is correct */
testF(RSSITests, macConversion) {
    uint64_t macDefined = 150;
    uint8_t bytes[6];
    memcpy(bytes, &macDefined, 6);
    assertEqual(handler->macToInt64(bytes), macDefined);
}

/* Test for exponential weighted average identity principle */
testF(RSSITests, ewaIdentity) {
    float rssi = -81;
    assertEqual(handler->exponentialWeightedAverage(rssi, rssi), rssi);
}

/* Test for exponential weighted average normal case */
testF(RSSITests, ewaChange) {
    handler->setWeight( 2 / 6.0 ); // Last 5 "values" matter
    assertEqual(handler->exponentialWeightedAverage(-81.0, -66.0), -76.0);
}

/* Tests that setup packet makes the correct packet, given correct size */
testF(RSSITests, normalPacketRightSize) {
    uint8_t standard_packet[13] = {
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0x46, 0x46, 0x46, 0x46, 0x46, 0xB0
    };
    uint8_t packet[13] = {};
    handler->setupPacket(packet, -80, "FF:FF:FF:FF:FF:FF");
    for (int i = 0; i < 13; i++) {
        assertEqual(packet[i], standard_packet[i]);
    }
}

/* Tests that setup packet makes the correct packet, given larger size */
testF(RSSITests, normalPacketLargeSize) {
    uint8_t standard_packet[13] = {
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0x46, 0x46, 0x46, 0x46, 0x46, 0xB0
    };
    uint8_t packet[15] = {};
    handler->setupPacket(packet, -80, "FF:FF:FF:FF:FF:FF");
    for (int i = 0; i < 13; i++) {
        assertEqual(packet[i], standard_packet[i]);
    }
    assertEqual(packet[13], 0x00);
    assertEqual(packet[14], 0x00);
}

/* Tests that setup packet makes the correct packet, given correct size */
testF(RSSITests, bulkPacketRightSize) {
    uint8_t standard_packet[17] = {
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0xB0, 0x00, 0x00, 0x00, 0xFF
    };
    handler->setupBulkPacket(-80, "FF:FF:FF:FF:FF:FF", 4278190080);
    uint8_t packet[17] = {};
    handler->getLog()->popFromLog(packet);
    for (int i = 0; i < 17; i++) {
        assertEqual(packet[i], standard_packet[i]);
    }
}

/* Tests that setup packet makes the correct packet, given large size */
testF(RSSITests, bulkPacketLargeSize) {
    uint8_t standard_packet[17] = {
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0xB0, 0x00, 0x00, 0x00, 0xFF
    };
    handler->setupBulkPacket(-80, "FF:FF:FF:FF:FF:FF", 4278190080);
    uint8_t packet[19] = {};
    handler->getLog()->popFromLog(packet);
    for (int i = 0; i < 17; i++) {
        assertEqual(packet[i], standard_packet[i]);
    }
    assertEqual(packet[13], 0x00);
    assertEqual(packet[14], 0x00);
}

/* Tests that setup packet properly adds to log */
testF(RSSITests, bulkPacketLogCheck) {
    handler->setupBulkPacket(-80, "FF:FF:FF:FF:FF:FF", 4278190080);
    handler->setupBulkPacket(-80, "FF:FF:FF:FF:FF:FF", 4278190080);
    handler->setupBulkPacket(-80, "FF:FF:FF:FF:FF:FF", 4278190080);
    handler->setupBulkPacket(-80, "FF:FF:FF:FF:FF:FF", 4278190080);
    
    assertEqual(handler->getLog()->logLength(), 4);
}

/* Tests sending bulk packet when log empty does nothing */
testF(RSSITests, bulkSendEmpty) {
    handler->sendBulkPacket(bulkChar);
    assertEqual(handler->getLog()->logLength(), 0);
    assertEqual((int) bulkChar->getValue().length(), 0);
    assertTrue(handler->getLog()->wasLogEmpty());
}

/* Tests sending bulk packet removes from log in correct order */
testF(RSSITests, bulkSendOrder) {
    handler->setupBulkPacket(-80, "FF:FF:FF:FF:FF:FF", 4278190080);
    handler->setupBulkPacket(-80, "EF:FF:FF:FF:FF:FF", 4278190080);
    handler->setupBulkPacket(-80, "DF:FF:FF:FF:FF:FF", 4278190080);
    handler->setupBulkPacket(-80, "CF:FF:FF:FF:FF:FF", 4278190080);
    char testFirst;

    handler->sendBulkPacket(bulkChar);
    testFirst = bulkChar->getValue().front();
    assertEqual(testFirst, 'F');

    handler->sendBulkPacket(bulkChar);
    testFirst = bulkChar->getValue().front();
    assertEqual(testFirst, 'E');

    handler->sendBulkPacket(bulkChar);
    testFirst = bulkChar->getValue().front();
    assertEqual(testFirst, 'D');

    handler->sendBulkPacket(bulkChar);
    testFirst = bulkChar->getValue().front();
    assertEqual(testFirst, 'C');
}

/* Tests check interaction return value for rssi < target */
testF(RSSITests, interactionLessThan) {
    assertFalse(handler->checkInteraction(0, 1, "", 0, false, rssiChar));
}

/* Tests check interaction return value for rssi == target */
testF(RSSITests, interactionEqual) {
    assertTrue(handler->checkInteraction(1, 1, "", 0, false, rssiChar));
}

/* Tests check interaction return value for rssi > target */
testF(RSSITests, interactionGreaterThan) {
    assertTrue(handler->checkInteraction(2, 1, "", 0, false, rssiChar));
}

/* Tests check interaction when connected == true */
testF(RSSITests, interactionConnected) {
    uint8_t standard_packet[13] = {
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0x46, 0x46, 0x46, 0x46, 0x46, 0xB0
    };
    handler->checkInteraction(-80, -80, "FF:FF:FF:FF:FF:FF", 0, true, rssiChar);
    for (int i = 0; i < 13; i++) {
        assertEqual((uint8_t) rssiChar->getValue()[i], standard_packet[i]);
    }
}

/* Tests check interaction when connected == false */
testF(RSSITests, interactionNotConnected) {
    uint8_t standard_packet[17] = {
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0x46, 0x46, 0x46, 0x46, 0x46, 0x46,
        0xB0, 0x00, 0x00, 0x00, 0xFF
    };
    uint8_t packet[17] {};
    handler->checkInteraction(-80, -80, "FF:FF:FF:FF:FF:FF", 4278190080, false, rssiChar);
    log->popFromLog(packet);
    for (int i = 0; i < 17; i++) {
        assertEqual(packet[i], standard_packet[i]);
    }
}

/* Tests average for inserting brand new mac */
testF(RSSITests, averageNew) {
    tree->insertNode(0, 0, 0);
    tree->insertNode(2, 0, 0);
    tree->insertNode(1, 0, 0);

    uint8_t address[8] = {0x00, 0x00, 0x01, 0x00, 0x00, 0x00};

    pNode node = handler->handleAverage(tree, 99.0, address, 0);
    assertEqual(node->rssi, 99.0);
    assertEqual(node->mac, handler->macToInt64(address));
}

/* Tests average for replacing mac since > 5000 elapsed */
testF(RSSITests, averageGreater) {
    uint8_t address[8] = {0x00, 0x00, 0x01, 0x00, 0x00, 0x00};

    pNode node = handler->handleAverage(tree, 99.0, address, 0);
    node = handler->handleAverage(tree, 90.0, address, 5001);
    assertEqual(node->rssi, 90.0);
    assertEqual(node->mac, handler->macToInt64(address));
}

/* Tests average for updating average of mac since 5000 elapsed */
testF(RSSITests, averageEqual) {
    uint8_t address[8] = {0x00, 0x00, 0x01, 0x00, 0x00, 0x00};

    pNode node = handler->handleAverage(tree, 99.0, address, 0);
    node = handler->handleAverage(tree, 90.0, address, 5000);
    assertEqual(node->rssi, handler->exponentialWeightedAverage(99.0, 90.0));
    assertEqual(node->mac, handler->macToInt64(address));
}

/* Tests average for updating average of mac since < 5000 elapsed */
testF(RSSITests, averageLess) {
    uint8_t address[8] = {0x00, 0x00, 0x01, 0x00, 0x00, 0x00};

    pNode node = handler->handleAverage(tree, 99.0, address, 0);
    node = handler->handleAverage(tree, 90.0, address, 4999);
    assertEqual(node->rssi, handler->exponentialWeightedAverage(99.0, 90.0));
    assertEqual(node->mac, handler->macToInt64(address));
}

void setup() {
    Serial.begin(115200);
}

void loop() {
    aunit::TestRunner::run();
}