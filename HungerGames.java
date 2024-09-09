package games;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts; // all districts in Panem.
    private TreeNode game; // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games takes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) {
        StdIn.setFile(filename); // open the file - happens only once here
        setupDistricts(filename);
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts(String filename) {
        int numDistricts = StdIn.readInt();
        for (int i = 0; i < numDistricts; i++) {
            int storeID = StdIn.readInt();
            District d = new District(storeID);
            districts.add(d);
        }
        // WRITE YOUR CODE HERE

    }

    /**
     * Reads the following from input file (continues to read from the SAME input
     * file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id,
     * effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by
     * districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPeople(String filename) {

        // WRITE YOUR CODE HERE
        int numPeople = StdIn.readInt();
        for (int i = 0; i <= numPeople; i++) {
            String process = StdIn.readLine();
            String[] dataCheck = process.split(" ");
            if (dataCheck.length == 6) {
                String firstName = dataCheck[0];
                String lastName = dataCheck[1];
                int bMonth = Integer.parseInt(dataCheck[2]);
                int age = Integer.parseInt(dataCheck[3]);
                int storeID = Integer.parseInt(dataCheck[4]);
                int effectNum = Integer.parseInt(dataCheck[5]);
                Person p = new Person(bMonth, firstName, lastName, age, storeID, effectNum);

                District end = null;
                for (District check : districts) {
                    if (check.getDistrictID() == storeID) {
                        end = check;
                    }
                }
                if (end != null) {
                    if (bMonth % 2 == 0) {
                        end.addEvenPerson(p);
                    } else {
                        end.addOddPerson(p);
                    }
                    if (age >= 12 && age < 18) {
                        p.setTessera(true);
                    }
                }
            }
        }
    }

    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
    public void addDistrictToGame(TreeNode root, District newDistrict) {
        this.districts.remove(newDistrict);

        if (game == null) {
            game = new TreeNode(newDistrict, null, null);
        } else {
            if (newDistrict.getDistrictID() < root.getDistrict().getDistrictID()) {
                if (root.getLeft() == null) {
                    root.setLeft(new TreeNode(newDistrict, null, null));

                } else {
                    addDistrictToGame(root.getLeft(), newDistrict);
                }
            } else if (newDistrict.getDistrictID() > root.getDistrict().getDistrictID()) {
                if (root.getRight() == null) {
                    root.setRight(new TreeNode(newDistrict, null, null));

                } else {
                    addDistrictToGame(root.getRight(), newDistrict);
                }
            }
        }
        // WRITE YOUR CODE HERE
    }

    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
    public District findDistrict(int id) {
        if (game == null) {
            return null;
        }

        TreeNode check = game;
        while (check != null) {
            int storeID = check.getDistrict().getDistrictID();

            if (id == storeID) {
                return check.getDistrict();
            } else if (id < storeID) {
                check = check.getLeft();
            } else {
                check = check.getRight();
            }

        }

        return null;

        // update this line
    }

    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * exist.
     * - If you can't find a person, use StdRandom.uniform(x) where x is the
     * respective
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler
     * to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */

    public DuelPair selectDuelers() {
        Person firstDueler = null;
        Person secondDueler = null;
        Stack<TreeNode> preOrderTraversal = new Stack<>();
        ArrayList<Integer> chosen = new ArrayList<>();

        // In this traversal, we only focus on finding duelers with tessera
        preOrderTraversal.push(game);
        while (!preOrderTraversal.isEmpty()) {
            if (firstDueler != null && secondDueler != null)
                break;

            TreeNode node = preOrderTraversal.pop();

            if (node.getRight() != null)
                preOrderTraversal.push(node.getRight());
            if (node.getLeft() != null)
                preOrderTraversal.push(node.getLeft());

            if (firstDueler == null) {
                ArrayList<Person> oddPopulation = node.getDistrict().getOddPopulation();
                for (Person person : oddPopulation) {
                    if (person.getTessera()) {
                        firstDueler = person;
                        chosen.add(node.getDistrict().getDistrictID());
                        break;
                    }
                }
            }

            if (secondDueler == null) {
                ArrayList<Person> evenPopulation = node.getDistrict().getEvenPopulation();
                for (Person person : evenPopulation) {
                    if (person.getTessera()) {
                        secondDueler = person;
                        chosen.add(node.getDistrict().getDistrictID());
                        break;
                    }
                }
            }
        }

        // We must do a second traversal of the tree to find random duelers if they
        // didn't exist previously
        preOrderTraversal.clear();
        preOrderTraversal.push(game);
        while (!preOrderTraversal.isEmpty()) {
            if (firstDueler != null && secondDueler != null)
                break;

            TreeNode node = preOrderTraversal.pop();

            if (node.getRight() != null)
                preOrderTraversal.push(node.getRight());
            if (node.getLeft() != null)
                preOrderTraversal.push(node.getLeft());

            if (chosen.contains(node.getDistrict().getDistrictID()))
                continue;

            if (firstDueler == null) {
                firstDueler = node.getDistrict().getOddPopulation().get(
                        StdRandom.uniform(node.getDistrict()
                                .getOddPopulation()
                                .size()));

                continue;
            }

            secondDueler = node.getDistrict().getEvenPopulation().get(
                    StdRandom.uniform(
                            node.getDistrict().getEvenPopulation().size()));
        }

        District oddPDistrict = findDistrict(firstDueler.getDistrictID());
        oddPDistrict.getOddPopulation().remove(firstDueler);

        District evenPDistrict = findDistrict(secondDueler.getDistrictID());
        evenPDistrict.getEvenPopulation().remove(secondDueler);

        return new DuelPair(firstDueler, secondDueler);
    }

    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not exist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
    public void eliminateDistrict(int id) {
        // Find district
        TreeNode currentNode = game;
        TreeNode parentNode = null;

        while (currentNode != null) {
            int currentDistrictId = currentNode.getDistrict().getDistrictID();

            if (currentDistrictId == id) {
                break;
            } else if (currentDistrictId > id) {
                parentNode = currentNode;
                currentNode = currentNode.getLeft();
            } else if (currentDistrictId < id) {
                parentNode = currentNode;
                currentNode = currentNode.getRight();
            }
        }

        // If currentNode is null, the district is not found
        if (currentNode == null) {
            return;
        }

        // Remove from districts array
        districts.remove(currentNode.getDistrict());

        // Case 1: No children
        if (currentNode.getLeft() == null && currentNode.getRight() == null) {
            if (parentNode == null) {
                game = null;
            } else if (parentNode.getLeft() == currentNode) {
                parentNode.setLeft(null);
            } else {
                parentNode.setRight(null);
            }
        }
        // Case 2: One child
        else if (currentNode.getLeft() == null || currentNode.getRight() == null) {
            TreeNode childNode;
            if (currentNode.getLeft() != null) {
                childNode = currentNode.getLeft();
            } else {
                childNode = currentNode.getRight();
            }

            if (parentNode == null) {
                game = childNode;
            } else if (parentNode.getLeft() == currentNode) {
                parentNode.setLeft(childNode);
            } else {
                parentNode.setRight(childNode);
            }
        }
        // Case 3: Two children
        else {
            TreeNode successor = currentNode.getRight();
            TreeNode successorParent = currentNode;

            while (successor.getLeft() != null) {
                successorParent = successor;
                successor = successor.getLeft();
            }

            if (successorParent != currentNode) {
                successorParent.setLeft(successor.getRight());
            } else {
                successorParent.setRight(successor.getRight());
            }

            currentNode.setDistrict(successor.getDistrict());
        }
    }

    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {
        Person oddD = pair.getPerson1();
        Person evenD = pair.getPerson2();

        if (oddD == null || evenD == null) {
            if (oddD != null) {
                District store = findDistrict(oddD.getDistrictID());
                store.addOddPerson(oddD);
            }
            if (evenD != null) {
                District store2 = findDistrict(evenD.getDistrictID());
                store2.addEvenPerson(evenD);
            }
        }
        Person win = oddD.duel(evenD);
        Person lose = win == oddD ? evenD : oddD;

        District w = this.findDistrict(win.getDistrictID());
        District l = this.findDistrict(lose.getDistrictID());

        if (win == oddD) {
            w.addOddPerson(oddD);
        } else if (win == evenD) {
            w.addEvenPerson(evenD);
        }

        int totalPopW = w.getEvenPopulation().size() + w.getOddPopulation().size();
        int totalPopL = l.getEvenPopulation().size() + l.getOddPopulation().size();

        if (totalPopW == 0 || w.getEvenPopulation().size() == 0 || w.getOddPopulation().size() == 0) {
            eliminateDistrict(w.getDistrictID());
        }
        if (totalPopL == 0 || l.getEvenPopulation().size() == 0 || l.getOddPopulation().size() == 0) {
            eliminateDistrict(l.getDistrictID());
        }

    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
