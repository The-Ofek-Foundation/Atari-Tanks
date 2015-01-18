# Atari-Tanks
The old Atari Tanks game

# Setup
- run the executable jar file
- or... compile:
- 1: GUITemplate.java
- 2: ArrayKit.java
- 3: AtariTanks.java
- then... either:
- run the html
- or... run the java program (java AtariTanks)

# Usage
- Red Move: WASD keys
- Blue Move: Arrow keys
- Red Shoot: Shift key
- Blue Shoot: Ctrl key
- New Game: 'r'
- Resize: Drag to resize the frame and then type 'c' to change (or start a new game)

# Gameplay
- Important Note: Avoid holding down keys when moving, because then your opponent might not be able to play. Simply click many times
- Shooting: Once the bullet is in the air, use the arrow keys to move the bullet as you would the tank
- Impact: Upon collision, you are pushed back and spun. If hit strongly enough, you might break through a wall and come back from the oppositve side of the map.
- Bouncy Walls: The bullets may or may not bounce of the wall in each game
- Capture the Flag: If there is a light-green squigle/s in the board, you gain points instead by the number of seconds in which you are on the squigle (they change color when you are on them)

# Making New Maps
- Go to the code to line 58, and set 'editLevels' to true
- Run the program
- Left click to place/remove solid walls (dragging works)
- Right click to place/remove breakable walls (dragging works)
- Middle click to place/remove hills for king of the hills
- Press 'p' when done to save file
- Your map will now randomly be chosen when playing maps
