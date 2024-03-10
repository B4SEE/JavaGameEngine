# Semester project for PJV (programming in Java) summer course 2023/2024

---

## Infinity Express - Destination Abyss
Welcome to the "Infinity Express - Destination Abyss"

"Infinity Express - Destination Abyss" represents both a game engine and a complete game, developed as a semester project for the PJV course.

---

## Status: In Development :construction:

&nbsp;

## Table of Contents
- [About](./README.md#-about)
- [Features](./README.md#gear-features)
- [Development checkpoints](./README.md#-development-checkpoints)
- [Requirements for project](./README.md#-requirements-for-project)
- [Wiki](./README.md#information_source-wiki)
- [Development process](./README.md#-development-process)
- [How to run](./README.md#-how-to-run)
- [Contact](./README.md#-contact)

&nbsp;

## 🔎 About

"Infinity Express - Destination Abyss" is an immersive 2D isometric game, challenging players to survive and ultimately escape from an infinite train journey.
As the train endlessly chugs along, wagons are dynamically generated, providing a unique experience with every playthrough.
Adding a personal touch, players can import custom wagon maps in TXT format, seamlessly integrating their creations into the game environment.

Developed in Java, the game leverages the power of the JavaFX library to deliver stunning graphics and smooth gameplay.
The game engine is crafted with flexibility in mind, offering easy modifiability for adding new game objects and mechanics.

&nbsp;

## :gear: Features

- **Endless Train:** Wagons are automatically generated, providing an endless gameplay experience.

- **Custom wagons:** Players have the ability to create and import their own wagons, which can then be found within the game.

- **Isometric View:** The game employs an isometric view, enhancing the visual experience with pixel textures for a unique and immersive gameplay perspective.

&nbsp;

## 🚩 Development checkpoints

**CP1 - Project Vision (until week 3 - 10/03, 23:59)** :heavy_check_mark:

<details><summary>Click to expand</summary>
On the GitLab project Wiki or as instructed by the instructor, upload a document, max. 1x A4, describing the term paper to BRUTE
the chosen topic, expected features and vision of the project
from the document it should be possible to imagine what the work will look like.
</details>

**CP2 - Object design (by week 8 - 12 April, 23:59)** :construction:

<details><summary>Click to expand</summary>
Documentation on the project Wiki from the user's perspective (e.g. manual)
on the Project Wiki, added documentation from the programmer's perspective (descriptions of classes, application states, technologies used, libraries, etc.); possible division of labor
source files in the project repository - skeletons of the main classes and interfaces, so that the proposed architecture is visible
</details>

**CP3 - Final presentation (by week 14 - 24 May, 23:59)** :construction:

<details><summary>Click to expand</summary>
Presentation of the whole work and the architecture of the application to the teacher.
Completed documentation from the user's perspective.
</details>

&nbsp;

## 📋 Requirements for project

<details><summary>Click to expand</summary>

- :construction: The game will be able to load a list of items from a file. These items will be given to the player at the beginning of the game. At the end of the game, the game will be able to save the list of items in the same format.


- :construction: Each level will be described in an external file in a suitable format - it is up to you what format you choose. For demonstration purposes, just create one or two levels of the game to demonstrate the functionality of all the elements, inventory, making an item from resources, and overcoming obstacles.


- :heavy_check_mark: If the level files are not "human-editable", an editor for these files must be created.


- :construction: A method of overcoming obstacles using hints and/or tools will be implemented within the game (the player will get the necessary information from NPCs during dialogue, use water to put out a fire in a path, build a bridge of iron over a lava field, etc.).


- :construction: The hero will be able to interact with other objects using some of the items he has collected (open a door with a key, smash a chest with a stick, etc.).


- :construction: The hero will be able to use the collected materials to make a certain object (make a lamp out of a light bulb and a battery, make bread out of wheat, water and fire, etc.).


- :construction: The game engine must be equipped with a GUI.
</details>

&nbsp;

## :information_source: Wiki

For information about the game mechanics, storyline, and technical specifications, please refer to [wiki](https://gitlab.fel.cvut.cz/B232_B0B36PJV/virycele/-/wikis/home).

&nbsp;

## 🏗️ Development process

<details><summary>Click to expand</summary>

### **Week 1 - 2**

<details><summary>Click to expand</summary>

Main [idea](https://gitlab.fel.cvut.cz/B232_B0B36PJV/virycele/-/wikis/home) of the game and initial planning. Basic model [schematic](https://gitlab.fel.cvut.cz/B232_B0B36PJV/virycele/-/wikis/home) with the main classes was created. First logic for the iso engine and basic rendering was implemented.

Video of the progress:

![Week 1-2](./projectDevelopmentFiles/videos/isometric_demo.mp4)

</details>

---

### **Week 3**

<details><summary>Click to expand</summary>

**05.03.24** - iso rendering was improved, collision detection added, player rendering and movement added, event handling added, main loop added;

Video of the progress:

![Week 3 05-03-2024](./projectDevelopmentFiles/videos/collision.mp4)

**06.03.24** - drawing queue added, all constant numbers were replaced with variables, rendering was optimized; started working on game wiki.

Video of the progress:

![Week 3 06-03-2024](./projectDevelopmentFiles/videos/drawing_queue_works.mp4)

![Week 3 06-03-2024-1](./projectDevelopmentFiles/videos/formulas_check.mp4)

**09.03.24** - loading maps from files (human-readable) added, player movement was improved, entity rendering, movement and logic added, player's and entity's methods were merged, doc comments added; final version of the game wiki was uploaded.

Video of the progress:

![Week 3 09-03-2024](./projectDevelopmentFiles/videos/entity_added.mp4)

**10.03.24** - slipping when colliding with a wall added, fighting logic added, player death logic added, basic reset and stopGame methods were added;

Video of the progress:

![Week 3 10-03-2024](./projectDevelopmentFiles/videos/wall_slipping.mp4)

![Week 3 10-03-2024-1](./projectDevelopmentFiles/videos/fight.mp4)

</details>

---

</details>

&nbsp;

## 🚀 How to run

! Project is not runnable yet. !

the instructions will be added later.

&nbsp;

## 📞 Contact

<details><summary>Click to expand</summary>

### **Teacher** - [RNDr. Ladislav Serédi](https://usermap.cvut.cz/profile/91b0ad62-3bc8-4227-a6c0-4481d2ebd12f)

📧 Email: [seredlad@fel.cvut.cz](mailto:seredlad@fel.cvut.cz)

&nbsp;

### **Author** - Eleonora Virych

📧 Email: [virycele@fel.cvut.cz](mailto:seredlad@fel.cvut.cz)

</details>