# Minesweeper Game - Tiger Edition- Iteration 1.0v
## By:

## ● יותם נחתומי-כץ 211718366

## ● מור לסמן 211621602

## ● ג'וליאנה אבו שקארה 207840216

## ● אברהם חיים ויינבלט 326161692

## ● עותמאן חליל 21277275

## 📋 Project Overview

**Minesweeper Game** is an innovative collaborative educational game that combines classic Minesweeper gameplay with an integrated quiz system. Developed by Group Tiger as part of a Software Engineering and Software Quality Assurance course, this project demonstrates practical application of software development methodologies, design patterns, and team collaboration.

### Game Concept
Two players work together on a shared grid-based Minesweeper board, where revealing cells not only uncovers mines but also triggers quiz questions. Players must balance strategic mine-sweeping with knowledge-based challenges to achieve the highest cooperative score.

---

## ✨ Key Features (Iteration 1)

### 🎮 Start Menu System
- Professional dark sci-fi themed main menu
- Four primary navigation options:
  - **New Game** - Configure and start a cooperative game
  - **Question Wizard** - Manage quiz question database
  - **History** - View past game sessions
  - **Exit** - Close application with confirmation

### ❓ Question Wizard
- **CSV Upload & Parsing** - Load questions from formatted CSV files
- **Data Validation** - Comprehensive error checking and reporting
- **Read-Only Table View** - Sortable display of all questions
- **Difficulty Badges** - Visual indicators for question difficulty levels
- **Error Recovery** -  Handling of malformed data with detailed feedback

### 🎯 New Game Setup
- **Player Configuration** - Enter names for both players
- **Difficulty Selection** - Choose from Easy, Medium, or Hard
- **Visual Difficulty Info** - Detailed stats for each difficulty level
- **Input Validation** - Ensures all required fields are completed

### 🎲 Game Board
- **Dynamic Grid** - Size adjusts based on selected difficulty
- **Cell Types** - Empty, Number, Mine, Question, Surprise
- **Cooperative Gameplay** - Shared lives and score system
- **Turn-Based Mechanics** - Players alternate turns

### 🏛️ Navigation System
- **Centralized Controller** - Single source of truth for navigation
- **Window Management** - Proper lifecycle handling for multiple windows
- **Seamless Transitions** - Smooth navigation between screens
- **Memory Efficient** - View caching and lazy loading

---

## 🏗️ Architecture

### MVC Design Pattern

The application strictly follows the **Model-View-Controller** architecture:

```
┌─────────────────────────────────────────────────┐
│                   APPLICATION                    │
├─────────────────────────────────────────────────┤
│                                                  │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐ │
│   │  MODEL   │    │   VIEW   │    │CONTROLLER│ │
│   │          │    │          │    │          │ │
│   │ Business │◄───│    UI    │◄───│Navigation│ │
│   │  Logic   │    │Components│    │ & Events │ │
│   │   Data   │───►│          │───►│          │ │
│   └──────────┘    └──────────┘    └──────────┘ │
│                                                  │
└─────────────────────────────────────────────────┘
```

### Package Structure

```
minesweeper/
│
├── model/                      # Business Logic & Data Layer
│   ├── MinesweeperApp.java           - Application coordinator
│   ├── GameSession.java              - Active game state
│   │   └── Difficulty (enum)         - Nested difficulty levels
│   ├── Board.java                    - Game board mechanics
│   ├── Cell.java                     - Individual cell logic
│   │   └── CellType (enum)           - Cell type definitions
│   ├── Question.java                 - Question data structure
│   ├── QuestionBank.java             - Question management & CSV parser
│   ├── QuestionDifficulty.java       - Question difficulty enum
│   ├── Difficulty.java               - Game difficulty enum
│   ├── GameHistory.java              - Single game record
│   ├── GameHistoryManager.java       - History collection manager
│   ├── User.java                     - User authentication data
│   └── Player.java                   - Player information
│
├── view/                       # Presentation Layer (Swing UI)
│   ├── StartMenuView.java            - Main menu interface
│   ├── NewGameView.java              - Game setup screen
│   ├── MinesweeperGame.java          - Game board display
│   ├── QuestionWizardView.java       - Question management UI
│   ├── HistoryView.java              - Game history display
│   ├── LoginView.java                - Authentication screen
│   │
│   └── components/                   - Reusable UI Components
│       ├── NeonButtonFactory.java    - Styled button creation
│       ├── NeonDialog.java           - Custom dialogs
│       ├── PlaceholderTextField.java - Enhanced text fields
│       ├── NeonTableCellRenderer.java - Table cell styling
│       └── NeonHeaderRenderer.java   - Table header styling
│
├── controller/                 # Control Layer (Business Logic)
│   ├── NavigationController.java     - Central navigation hub
│   ├── StartMenuController.java      - Start menu event handling
│   ├── QuestionWizardController.java - Question wizard logic
│   ├── GameController.java           - Game session management
│   ├── GameHistoryLogic.java         - History management
│   ├── LoginController.java          - Authentication logic
│   └── QuestionWizardLauncher.java   - Wizard initialization
│
├── Data/                       # Persistent Storage
│   ├── UserData.json                 - User credentials
│   └── history.json                  - Game session records
│
└── Main.java                   # Application entry point
```

---

## 🎨 User Interface Design

### Dark Sci-Fi Theme

The application features a consistent **cyberpunk-inspired** aesthetic:

**Color Palette:**
- **Background Dark**: `#1a1a2e` - Deep navy
- **Primary**: `#0f3460` - Rich blue
- **Accent**: `#16213e` - Dark blue
- **Neon Cyan**: `#00d4ff` - Bright cyan
- **Neon Purple**: `#9d4edd` - Vibrant purple

**Typography:**
- **Font Family**: Arial, Sans-Serif
- **Title Size**: 24pt Bold
- **Button Text**: 16pt
- **Body Text**: 14pt

**Visual Effects:**
- Neon glow effects on buttons
- Rounded corners (8px radius)
- Gradient backgrounds
- Hover and press animations
- Colored difficulty badges

### Custom UI Components

1. **NeonButtonFactory** - Creates styled buttons with:
   - Primary, secondary, and danger variants
   - Hover glow effects
   - Press animations
   - Consistent styling

2. **NeonDialog** - Custom dialog boxes with:
   - Themed backgrounds
   - Styled buttons
   - Info, warning, error variants

3. **PlaceholderTextField** - Enhanced text inputs with:
   - Gray placeholder text
   - Focus highlighting
   - Consistent styling

4. **Table Renderers** - Styled table components with:
   - Alternating row colors
   - Difficulty badges
   - Neon headers

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK**: Version 8 or higher
- **IDE**: IntelliJ IDEA (recommended) or any Java IDE
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 1GB RAM
- **Disk Space**: 50MB

### Installation

#### Using IntelliJ IDEA

```bash
# 1. Clone or extract the project
cd path/to/Mine_Sweeper

# 2. Open IntelliJ IDEA
File → Open → Select Mine_Sweeper directory

# 3. Configure Project SDK
File → Project Structure → Project SDK: Java 8+

# 4. Mark src as Sources Root
Right-click src → Mark Directory as → Sources Root

# 5. Run the application
Right-click Main.java → Run 'Main.main()'
```

#### Using Command Line

```bash
# Navigate to project directory
cd Mine_Sweeper

# Compile all source files
javac -d out/production/Mine_Sweeper \
      -sourcepath src \
      $(find src -name "*.java")

# Run the application
java -cp out/production/Mine_Sweeper minesweeper.Main
```

### First Launch

Upon running the application, you'll see:

```
╔═══════════════════════════════════════╗
║   MINESWEEPER - TIGER EDITION v1.0   ║
║          Group Tiger Project          ║
╚═══════════════════════════════════════╝
```

The main menu will appear with four options ready for navigation.

---

## 💡 Usage Guide

### Starting a New Game

1. Click **"NEW GAME"** from the start menu
2. Enter **Player 1 Name** in the first text field
3. Enter **Player 2 Name** in the second text field
4. Select a **difficulty level**:
   - **Easy**: 9×9 grid, 10 mines, 6 questions
   - **Medium**: 13×13 grid, 26 mines, 7 questions
   - **Hard**: 16×16 grid, 44 mines, 11 questions
5. Click **"START GAME"** to begin
6. Click **"BACK"** to return to main menu

### Managing Questions

1. Click **"QUESTION WIZARD"** from the start menu
2. Click **"Upload CSV"** button
3. Navigate to your CSV file (see format below)
4. Select the file and click Open
5. Questions will load and display in the table
6. Click column headers to sort
7. Click **"Reload"** to refresh from same file
8. Click **"BACK TO START"** to return to menu

### CSV File Format

```csv
ID,Question,Difficulty,A,B,C,D,Correct Answer
1,What is 2+2?,1,3,4,5,6,B
2,Capital of France?,2,London,Paris,Berlin,Rome,B
3,Largest planet?,3,Earth,Mars,Jupiter,Saturn,C
```

**Field Requirements:**
- **ID**: Positive integer
- **Question**: Non-empty text
- **Difficulty**: 1 (Easy), 2 (Medium), 3 (Hard), 4 (Expert)
- **A, B, C, D**: Answer options (non-empty)
- **Correct Answer**: Letter A, B, C, or D

### Viewing History

1. Click **"HISTORY"** from the start menu
2. View past game sessions in table format
3. See difficulty, players, scores, and dates
4. Click **"BACK"** to return to main menu

---

## 🎮 Game Mechanics

### Difficulty Settings

| Level | Grid Size | Mines | Questions | Surprises | Lives | Activation Cost |
|-------|-----------|-------|-----------|-----------|-------|-----------------|
| **Easy** | 9×9 | 10 | 6 | 2 | 10 | 5 |
| **Medium** | 13×13 | 26 | 7 | 3 | 8 | 8 |
| **Hard** | 16×16 | 44 | 11 | 4 | 6 | 12 |

### Cell Types

- **Empty**: Safe cells with no adjacent mines
- **Number**: Shows count of adjacent mines (1-8)
- **Mine**: Deducts lives when revealed
- **Question**: Triggers quiz question
- **Surprise**: Random bonus or penalty

### Scoring System

**Easy Mode:**
- Safe cell: +1 point
- Mine hit: -5 points
- Correct answer: +3 points
- Wrong answer: -2 points

**Medium Mode:**
- Safe cell: +2 points
- Mine hit: -10 points
- Correct answer: +5 points
- Wrong answer: -3 points

**Hard Mode:**
- Safe cell: +3 points
- Mine hit: -15 points
- Correct answer: +7 points
- Wrong answer: -4 points

### Win Conditions

- **Victory**: Reveal all safe cells without running out of lives
- **Defeat**: Lives reach zero
- **Cooperative**: Both players share the same score and lives

---

## 🛠️ Technical Details

### Technologies Used

- **Language**: Java 8+
- **UI Framework**: Swing (javax.swing)
- **Data Format**: CSV for questions, JSON for user data
- **Build Tool**: Manual compilation or IDE (IntelliJ IDEA)
- **Version Control**: Git

### Design Patterns

1. **Model-View-Controller (MVC)**
   - Clear separation of concerns
   - Independent layers
   - Testable components

2. **Factory Pattern**
   - `NeonButtonFactory` for UI component creation
   - Consistent component styling

3. **Observer Pattern**
   - Event listeners for user interactions
   - Decoupled event handling

4. **Singleton-like Behavior**
   - `NavigationController` as central navigation hub
   - Single point of coordination

### Code Quality

- **JavaDoc Documentation**: All public methods documented
- **Error Handling**: Comprehensive try-catch blocks
- **Input Validation**: Defensive programming throughout
- **Thread Safety**: EDT-aware UI updates using `SwingUtilities.invokeLater()`
- **Naming Conventions**: Clear, descriptive names
- **Package Organization**: Logical separation by responsibility

### Window Lifecycle Management

The navigation system properly manages window lifecycles:

- **DISPOSE_ON_CLOSE**: Prevents premature application termination
- **Custom Window Listeners**: Handle window closing events
- **Navigation Callbacks**: Restore main frame when returning
- **Thread-Safe Updates**: All UI changes on Event Dispatch Thread

---

## 📊 Project Statistics

### Code Metrics

- **Total Java Files**: 33
- **Total Lines of Code**: ~5,600
- **Model Classes**: 11
- **View Classes**: 6 main + 5 components
- **Controller Classes**: 7
- **Entry Points**: 3 (Main, QuestionWizardApp, LoginViewApp)

### File Distribution

```
Model Layer:        ~1,800 LOC (32%)
View Layer:         ~2,500 LOC (45%)
Controller Layer:   ~1,300 LOC (23%)
```

### Complexity Distribution

- **Simple** (< 100 LOC): Player, User, Cell
- **Moderate** (100-300 LOC): Most controllers and views
- **Complex** (300+ LOC): NavigationController, QuestionBank, MinesweeperGame

---

## 🔍 Key Features Explained

### Question Wizard CSV Parser

The `QuestionBank` class provides robust CSV parsing:

**Features:**
- UTF-8 encoding support with BOM handling
- Quoted field parsing (handles commas in text)
- Header validation
- Row-by-row error reporting
- Graceful degradation (loads valid rows, reports errors)

**Error Handling:**
- File not found
- Invalid headers
- Wrong column count
- Invalid ID format
- Invalid difficulty values
- Invalid correct answer letters
- Empty required fields

### Navigation System

The `NavigationController` manages all screen transitions:

**Features:**
- Panel-based views (embedded in main frame)
- Frame-based views (separate windows)
- View caching (lazy initialization)
- Back button functionality
- Window restoration on return

**Navigation Flows:**
```
Start Menu → New Game → Game Board → Back to Start Menu
Start Menu → Question Wizard → Back to Start Menu
Start Menu → History → Back to Start Menu
```

### Game Board Mechanics

The `Board` class handles game logic:

**Initialization:**
1. Creates grid of cells
2. Randomly places mines
3. Calculates adjacent mine counts
4. Places special tiles (questions, surprises)

**Operations:**
- Cell revelation
- Flag toggling
- Adjacent mine counting
- Win condition checking

---

## 🎓 Software Engineering Practices

### Agile Methodology

The project follows **Scrum** practices:

- **Iterations**: Time-boxed development cycles
- **User Stories**: Feature definitions with acceptance criteria
- **Sprint Planning**: Task estimation and commitment
- **Sprint Review**: Demo working software
- **Retrospectives**: Continuous improvement

### Version Control

**Git Workflow:**
```bash
# Feature development
git checkout -b feature/question-wizard
git commit -m "feat: implement CSV upload"
git push origin feature/question-wizard

# Bug fixes
git checkout -b fix/navigation-bug
git commit -m "fix: resolve back button issue"
```

### Documentation Standards

- **JavaDoc**: All public APIs documented
- **Inline Comments**: Complex logic explained
- **README**: Comprehensive project documentation
- **Architecture Diagrams**: Visual system overview

---

## 🔧 Configuration

### File Paths

```java
// Question CSV files
resources/Questions/Questions.csv
resources/Questions/sample_questions.csv

// User data
src/minesweeper/Data/UserData.json
src/minesweeper/Data/history.json
```

### Window Settings

```java
// Main window
Width: 1000px
Height: 700px

// Question Wizard
Width: 900px
Height: 600px
```

### Theme Colors

```java
// Background
BG_DARK:    #1a1a2e
BG_PRIMARY: #0f3460

 Accents
NEON_CYAN:   #00d4ff
NEON_PURPLE: #9d4edd
```

---

## 🐛 Troubleshooting

### Application Won't Start

**Issue**: Nothing happens when running Main.java

**Solution**:
- Verify Java 8+ is installed: `java -version`
- Check project SDK configuration
- Ensure `src` is marked as Sources Root
- Look for compilation errors in IDE

### CSV Won't Load

**Issue**: Error when uploading CSV file

**Solution**:
- Verify CSV format matches specification
- Check for proper UTF-8 encoding
- Ensure all required fields are present
- Look at error messages for specific issues

### Navigation Issues

**Issue**: Screens not switching properly

**Solution**:
- Check console for error messages
- Verify all views are properly initialized
- Ensure window listeners are attached
- Try restarting the application

### Display Problems

**Issue**: UI elements not showing correctly

**Solution**:
- Verify screen resolution (minimum 1024×768)
- Check for custom theme compatibility
- Update graphics drivers
- Try different look and feel

---

## 👥 Team Information
**Date of submission:** 30.11.25  
**Project**: Minesweeper Game - Tiger Edition  
**Group**: Tiger  
**Course**: הנדסת תוכנה ואבטחת איכות 26 3610 א01
**Institution**: Information Systems BSc Program  
**Semester**: א01
**Iteration**: 1  
**Version**: 1.0

### Roles

- **Product Owner**: Osman
- **Scrum Master**: Avi
- **Devolpers**: Yotam, Mor, Juliana

---

## 📚 Additional Resources

### Project Documents

- **SRS Document**: Software Requirements Specification
- **SDP Document**: Software Development Plan
- **Class Diagram**: UML class structure
- **Lecture Materials**: Course reference materials
- **Github Repositry**: https://github.com/julianaabushkara/Mine_Sweeper_Tiger
- **Drive Link**: https://drive.google.com/drive/folders/11cKiURO1uhlBGhzW74zHev8tugCUrmYD?usp=sharing

### Learning Resources

- **Java Swing Tutorial**: https://docs.oracle.com/javase/tutorial/uiswing/
- **Git Workflow**: https://www.atlassian.com/git/tutorials/comparing-workflows

---

## 🔮 Future Development

### Planned Features

- Complete game mechanics implementation
- Question answering during gameplay
- Advanced score tracking
- Multiplayer turn management
- Sound effects and animations
- Leaderboard system
- User authentication
- Question editing capabilities
- Advanced statistics
- Mobile support

### Enhancement Ideas

- Online multiplayer
- Custom difficulty levels
- Achievement system
- Theme customization
- Question categories
- Time-based challenges
- Replay functionality
- Export game statistics

---

## 📝 Change Log

### Version 1.0 (Iteration 1)
- ✅ Start menu navigation
- ✅ Question Wizard with CSV upload
- ✅ New Game setup screen
- ✅ Game board framework
- ✅ History view framework
- ✅ Dark sci-fi theme
- ✅ Custom UI components
- ✅ MVC architecture
- ✅ Navigation system
- ✅ Error handling

---

## 📜 License

This project is developed as part of an academic course.  
All rights reserved to Group Tiger and the academic institution.


---

## 🙏 Acknowledgments

- Course instructors George and Ali for guidance and requirements
- Java Swing community for UI patterns
- Team members for collaboration
- Beta testers for feedback
- Tiger conservation supporters
---

## 📧 Contact & Support

For questions, issues, or contributions:
- Contact course instructor
- Consult teaching assistants
- Review project documentation
- Check troubleshooting guide

---

**Built with ❤️ by Group Tiger**

**Version**: 1.0.0  
**Last Updated**: November 2024  
**Status**: Iteration 1 Complete ✅

---

*This README provides comprehensive documentation for the Minesweeper Quiz Game project. For additional details, consult the UML class diagram and supplementary project documents.*
