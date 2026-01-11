# Minesweeper Game - Tiger Edition - Iteration 3.0

# Important note: This represents the third and final iteration of the Minesweeper Quiz Game. Iteration 3 focuses on user authentication, personalized question banks, advanced history filtering with Strategy pattern implementation, refined turn mechanics, audio feedback, and final polish features.
# **Please see entire implementation plan in SDP_Tiger**

## By:

## ● יותם נחתומי-כץ 211718366

## ● מור לסמן 211621602

## ● ג'וליאנה אבו שקארה 207840216

## ● אברהם חיים ויינבלט 326161692

## ● עותמאן חליל 21277275

## 📋 Project Overview

**Minesweeper Game - Tiger Edition** is an innovative collaborative educational game that combines classic Minesweeper gameplay with an integrated quiz system. Developed by Group Tiger as part of a Software Engineering and Software Quality Assurance course, this project demonstrates practical application of software development methodologies, design patterns, and team collaboration through three complete iterations.

### Game Concept
Two players work together on a shared grid-based Minesweeper board, where revealing cells not only uncovers mines but also triggers quiz questions. Players must balance strategic mine-sweeping with knowledge-based challenges to achieve the highest cooperative score. **Iteration 3** adds user authentication, personalized experiences, and professional-grade polish.

---

## ✨ Key Features (Iteration 3)

### 🔐 Authentication System - **NEW**
- **User Login** - Secure username and password authentication
- **Create Account** - New user registration with validation
  - Username uniqueness checking
  - Password confirmation matching
  - Security question and answer setup
- **Password Recovery** - "Forgot Password?" functionality
  - Security question verification
  - Password retrieval dialog
- **Password Visibility Toggle** - Eye icon to show/hide password
- **Session Management** - Logged-in user persisted throughout session
- **User Data Persistence** - JSON-based user storage in `~/.minesweeper/UserData.json`
- **Input Validation** - Comprehensive checks for all authentication fields

### 👤 User-Specific Question Banks - **NEW**
- **Per-User CSV Files** - Each user has their own question bank
  - Questions stored as `questions_{username}.csv`
  - Complete isolation between users
  - Automatic file creation on first upload
- **Question Wizard Integration** - Seamless user-aware question management
  - Upload CSV specific to logged-in user
  - View only your own questions
  - Add/Edit/Delete operations affect only your question bank
- **Game Integration** - Games automatically use logged-in user's questions
  - No question leakage between users
  - Maintains question bank integrity
- **Persistent Storage** - Questions saved to user directory (`~/.minesweeper/`)

### 📊 Advanced History Filtering - **NEW** (Strategy Pattern)
- **User Filter** - View games by specific player
  - Dropdown with all registered users
  - "ALL" option to show all games
- **Result Filter** - Filter by game outcome
  - WIN: Show only victorious games
  - LOSE: Show only defeats
  - ALL: Display both outcomes
- **Difficulty Filter** - Filter by game difficulty level
  - EASY, MEDIUM, HARD options
  - ALL: Display all difficulty levels
- **Score Sorting** - Dynamic score-based ordering
  - Sort by Score Ascending
  - Sort by Score Descending
  - JSON order (original chronological)
  - Cyclical button toggling
- **Combined Filtering** - Apply multiple filters simultaneously
- **Strategy Pattern Implementation** - Clean, extensible filtering architecture
  - `HistoryFilterStrategy` interface
  - Concrete strategies: `UserFilterStrategy`, `WinLoseFilterStrategy`, `DifficultyFilterStrategy`
  - `HistorySortStrategy` for sorting operations
  - Easy to add new filtering criteria

### 🎮 Turn Mechanics Refinement - **NEW**
- **Flagging Behavior** - Right-click flagging no longer switches turns
  - Players can flag multiple cells consecutively
  - Strategic flagging without turn penalty
  - Encourages thoughtful flag placement
- **Left-Click Turn Switch** - Only revealing cells switches turns
  - Empty cells → reveal and switch
  - Number cells → reveal and switch
  - Mine cells → reveal, lose life, switch
  - Question cells → answer, then switch
- **Turn Indicator** - Clear visual feedback of whose turn it is
- **Consistent Game Flow** - Smooth turn-based progression

### 🔊 Audio Feedback - **NEW**
- **Sound Effects (SFX)**
  - Explosion sound when revealing mines (`boom.wav`)
  - Click sounds for button interactions
  - Resource-based audio loading from classpath
- **Background Music**
  - Victory music on game win (`win.wav`)
  - Defeat music on game loss (`lose.wav`)
  - Automatic music switching based on game state
- **Audio Manager** - Centralized audio system
  - Singleton pattern implementation
  - Efficient audio caching
  - Graceful handling of missing audio files
  - Proper resource cleanup on shutdown
- **Audio Integration** - Seamless audio throughout game lifecycle
  - Click sounds on all interactive buttons
  - Mine explosion on reveal
  - Victory/defeat music at game end

### 🚪 EXIT Button Functionality - **NEW**
- **Proper Application Termination** - Red neon-styled EXIT button
  - Located on start menu
  - Clean shutdown of all resources
  - AudioManager cleanup
  - Window disposal
- **Visual Styling** - Distinct red neon theme for EXIT
- **Confirmation** - Safe application closure

### 🎯 Question Wizard - ENHANCED
- **User-Aware CRUD Operations** - All operations scoped to current user
- **CSV Upload** - Uploads saved to user-specific file
- **Question Management** - Add, edit, delete questions in your bank
- **Data Validation** - Comprehensive error checking
- **Visual Feedback** - Clear indication of success/failure
- **Difficulty Badges** - Color-coded difficulty indicators

### 🏁 Game History - ENHANCED
- **User-Scoped History** - Games tagged with player usernames
- **Enhanced Filtering** - Multiple simultaneous filter application
- **Sort Functionality** - Flexible score-based ordering
- **Persistent Storage** - JSON-based history with user information

### 🏛️ Navigation System - ENHANCED
- **Login-First Flow** - Application starts at login screen
- **Session-Based Navigation** - User context maintained throughout
- **Centralized Controller** - Consistent navigation patterns
- **Window Management** - Proper lifecycle handling

---

## 🏗️ Architecture

### MVC Design Pattern

The application strictly follows the **Model-View-Controller** architecture with Strategy pattern for filtering:

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
│   │   Data   │───►│          │───►│ Gameplay │ │
│   │ Strategy │    │  Audio   │    │  Auth    │ │
│   └──────────┘    └──────────┘    └──────────┘ │
│                                                  │
└─────────────────────────────────────────────────┘
```

### Package Structure (Iteration 3)

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
│   ├── GameHistory.java              - Single game record [ENHANCED]
│   ├── GameHistoryManager.java       - History collection manager
│   ├── User.java                     - User authentication data [NEW]
│   ├── SessionContext.java           - Current user session [NEW]
│   ├── Player.java                   - Player information
│   │
│   ├── audio/                        - Audio Management [NEW]
│   │   ├── AudioManager.java         - Singleton audio controller
│   │   └── SoundPlayer.java          - Individual sound playback
│   │
│   └── HistoryFilterStrategy/        - Strategy Pattern [NEW]
│       ├── HistoryFilterStrategy.java      - Filter interface
│       ├── UserFilterStrategy.java         - Filter by user
│       ├── WinLoseFilterStrategy.java      - Filter by result
│       ├── DifficultyFilterStrategy.java   - Filter by difficulty
│       ├── HistorySortStrategy.java        - Sort interface
│       ├── ScoreAscSortStrategy.java       - Sort ascending
│       └── ScoreDescSortStrategy.java      - Sort descending
│
├── view/                       # Presentation Layer (Swing UI)
│   ├── LoginView.java                - Authentication screen [NEW]
│   ├── StartMenuView.java            - Main menu interface [ENHANCED]
│   ├── NewGameView.java              - Game setup screen
│   ├── MinesweeperGame.java          - Game board display [ENHANCED]
│   ├── QuestionWizardView.java       - Question management UI [ENHANCED]
│   ├── HistoryView.java              - Game history display [MAJOR ENHANCEMENTS]
│   ├── QuestionEditorDialog.java     - Question CRUD dialog
│   │
│   └── components/                   - Reusable UI Components
│       ├── NeonButtonFactory.java    - Styled button creation
│       ├── NeonDialog.java           - Custom dialogs
│       ├── PlaceholderTextField.java - Enhanced text fields
│       ├── NeonTableCellRenderer.java - Table cell styling
│       ├── NeonHeaderRenderer.java   - Table header styling
│       ├── NeonTooltip.java          - Custom tooltip styling
│       ├── NeonComboBoxFactory.java  - Combo box styling [NEW]
│       └── NeonScrollBarUI.java      - Scroll bar styling [NEW]
│
├── controller/                 # Control Layer (Business Logic)
│   ├── LoginController.java          - Authentication logic [NEW]
│   ├── NavigationController.java     - Central navigation hub [ENHANCED]
│   ├── StartMenuController.java      - Start menu event handling
│   ├── QuestionWizardController.java - Question wizard logic [ENHANCED]
│   ├── GameController.java           - Game session management [ENHANCED]
│   ├── GameHistoryLogic.java         - History management [ENHANCED]
│   └── AudioBinder.java              - Audio event binding [NEW]
│
├── test/                       # Testing Suite
│   ├── WhiteBoxCSVValidationTest.java    - White box tests (WB-01)
│   ├── BlackBoxCSVRowFormatTest.java     - Black box tests (BB-01)
│   └── CSVParsingJUnitTest.java          - JUnit unit tests (UT-01)
│
├── Data/                       # Persistent Storage
│   └── UserData.json                 - User credentials [NEW]
│
├── assets/                     # Media Resources [NEW]
│   ├── audio/
│   │   ├── sfx/                      - Sound effects
│   │   │   ├── boom.wav              - Mine explosion
│   │   │   └── click.wav             - Button clicks
│   │   └── music/                    - Background music
│   │       ├── win.wav               - Victory music
│   │       └── lose.wav              - Defeat music
│   └── images/                       - Visual assets
│       └── eye.png                   - Password visibility toggle
│
└── Main.java                   # Application entry point
```

---

## 🎨 User Interface Design

### Iteration 3 UI Enhancements

**Login Screen**:
- Clean, professional authentication interface
- Password visibility toggle with eye icon
- Prominent "CREATE ACCOUNT" and "Forgot Password?" options
- Consistent neon cyan theming
- Validation feedback dialogs

**History Screen**:
- Filter controls row with three combo boxes
  - User dropdown (dynamically populated)
  - Result dropdown (ALL/WIN/LOSE)
  - Difficulty dropdown (ALL/EASY/MEDIUM/HARD)
- "SORT BY SCORE" button with cyclical modes
- Enhanced table with filtered results
- Refresh and back buttons

**Start Menu**:
- Red neon-styled EXIT button
- Visual distinction from other navigation buttons
- Audio-enabled button interactions

### Color Scheme Consistency

**Iteration 3 maintains the dark sci-fi theme**:
- Background Dark: `#0F0F14` (Deep navy)
- Primary: `#1A1A2E` (Rich blue)
- Neon Cyan: `#00B4FF` (Bright cyan for primary actions)
- Neon Purple: `#9D4EDD` (Vibrant purple for secondary actions)
- Neon Red: `#FF073A` (Bright red for EXIT)
- Success Green: `#00FF41` (Confirmation)
- Warning Red: `#FF1744` (Errors)

---

## 🎯 Design Patterns

### Iteration 3 Design Pattern Implementations

#### Strategy Pattern - **NEW** (History Filtering)
**Purpose**: Enable flexible, interchangeable filtering algorithms for game history

**Implementation**:
```java
// Strategy Interface
public interface HistoryFilterStrategy {
    List<GameHistory> filter(List<GameHistory> history);
}

// Concrete Strategies
public class UserFilterStrategy implements HistoryFilterStrategy {
    private String username;
    
    @Override
    public List<GameHistory> filter(List<GameHistory> history) {
        return history.stream()
            .filter(h -> h.getPlayerA().equals(username) || 
                        h.getPlayerB().equals(username))
            .collect(Collectors.toList());
    }
}

public class WinLoseFilterStrategy implements HistoryFilterStrategy {
    private String resultType; // "WIN" or "LOSE"
    
    @Override
    public List<GameHistory> filter(List<GameHistory> history) {
        return history.stream()
            .filter(h -> h.getResult().equals(resultType))
            .collect(Collectors.toList());
    }
}

public class DifficultyFilterStrategy implements HistoryFilterStrategy {
    private String difficulty; // "EASY", "MEDIUM", "HARD"
    
    @Override
    public List<GameHistory> filter(List<GameHistory> history) {
        return history.stream()
            .filter(h -> h.getDifficulty().equals(difficulty))
            .collect(Collectors.toList());
    }
}
```

**Benefits**:
- Easy to add new filtering criteria
- Filters can be combined dynamically
- Clean separation of concerns
- Open/Closed Principle compliance

**Usage**:
```java
List<GameHistory> filtered = history;

if (!userFilter.equals("ALL")) {
    filtered = new UserFilterStrategy(userFilter).filter(filtered);
}
if (!resultFilter.equals("ALL")) {
    filtered = new WinLoseFilterStrategy(resultFilter).filter(filtered);
}
if (!difficultyFilter.equals("ALL")) {
    filtered = new DifficultyFilterStrategy(difficultyFilter).filter(filtered);
}
```

#### Singleton Pattern (AudioManager) - **NEW**
**Purpose**: Centralized audio management with single instance

**Implementation**:
```java
public class AudioManager {
    private static AudioManager instance;
    
    public static AudioManager get() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }
    
    private AudioManager() {} // Private constructor
    
    public void playSfx(String path) { /* ... */ }
    public void playMusic(String path) { /* ... */ }
    public void stopMusic() { /* ... */ }
}
```

**Benefits**:
- Single audio resource manager
- Prevents audio conflicts
- Efficient resource caching
- Easy global access

#### Factory Pattern (Continues from Previous Iterations)
- `BoardFactory` for board generation
- `NeonButtonFactory` for UI component creation
- `NeonComboBoxFactory` for styled dropdowns

#### Observer Pattern (Continues from Previous Iterations)
- Board state updates notify UI components
- Game state changes trigger view refreshes

#### MVC Pattern (Continues from Previous Iterations)
- Strict separation of concerns
- Model: Business logic and data
- View: UI components and presentation
- Controller: Event handling and coordination

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher
- **IDE**: IntelliJ IDEA, Eclipse, or NetBeans recommended
- **Git**: For version control and collaboration
- **Operating System**: Windows, macOS, or Linux

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/julianaabushkara/Mine_Sweeper_Tiger.git
   cd Mine_Sweeper_Tiger
   ```

2. **Open in IDE**
   - Import as a Java project
   - Ensure JDK 17+ is configured
   - Build the project

3. **Run the Application**
   ```bash
   # Option 1: Run from IDE
   # Right-click Main.java → Run

   # Option 2: Run from JAR
   java -jar Mine_Sweeper_Tiger.jar

   # Option 3: Run from command line
   cd src
   javac minesweeper/Main.java
   java minesweeper.Main
   ```

### First Launch (Iteration 3)

1. **Login Screen Appears**
   - Application starts at authentication screen (NEW)

2. **Create Your Account**
   - Click "CREATE ACCOUNT" button
   - Enter unique username
   - Enter password (twice for confirmation)
   - Select security question
   - Provide security answer
   - Click "REGISTER"

3. **Login**
   - Enter your username and password
   - Click "LOGIN" to access the game

4. **Upload Questions** (First Time)
   - Navigate to "Question Wizard"
   - Click "UPLOAD CSV"
   - Select your questions CSV file
   - Questions saved to your personal question bank

5. **Start Playing**
   - Return to Start Menu
   - Click "NEW GAME"
   - Enter player names and select difficulty
   - Begin cooperative gameplay!

---

## 📖 How to Play

### Game Setup (With Authentication)

1. **Login or Register**
   - Start application → Login screen
   - Create account or login with existing credentials

2. **Upload Your Questions**
   - Navigate to Question Wizard
   - Upload CSV file (saved to `questions_{your_username}.csv`)

3. **Configure New Game**
   - Click "NEW GAME" from Start Menu
   - Enter Player A name (1-20 characters)
   - Enter Player B name (1-20 characters)
   - Select difficulty (Easy/Medium/Hard)
   - Click "START GAME"

### Gameplay Mechanics (With Refinements)

**Turn-Based System**:
- Player A and Player B alternate turns
- **Left-click** reveals cells → **switches turn**
- **Right-click** flags cells → **keeps your turn** *(NEW)*
- Current player highlighted in HUD

**Cell Types**:
- **Empty**: Auto-reveals connected empty areas
- **Number**: Shows adjacent mine count
- **Mine**: Reduces shared lives by 1
- **Question**: Opens quiz overlay, answer affects score/lives
- **Surprise**: Random bonus or penalty

**Flagging Strategy** *(Iteration 3 Enhancement)*:
- Right-click suspected mines
- Flag multiple cells in a row without turn switch
- Correct flags (+1 point), wrong flags (-3 points)
- Cell is marked but turn remains with you

**Scoring**:
- Reveal safe cell: +1 point
- Correct flag: +1 point
- Correct answer (question): +1 to +3 (difficulty-based)
- Wrong flag: -3 points
- Wrong answer: -1 life

**Audio Feedback** *(NEW)*:
- Mine explosion: Boom sound effect
- Button clicks: Click sound
- Victory: Uplifting music
- Defeat: Somber music

**Win Conditions**:
- Reveal all safe cells without losing all lives
- Final score = points + (remaining lives × difficulty multiplier)

**Loss Conditions**:
- Shared lives reach 0

### History Management (Enhanced Filtering)

**View Past Games**:
1. Click "HISTORY" from Start Menu
2. Use filter dropdowns:
   - **User**: Select player name or "ALL"
   - **Result**: WIN, LOSE, or "ALL"
   - **Difficulty**: EASY, MEDIUM, HARD, or "ALL"
3. Click "SORT BY SCORE" to cycle sort order
   - JSON order (chronological)
   - Score Ascending
   - Score Descending
4. View filtered/sorted results
5. Click "REFRESH" to reload latest data

**Filter Combinations**:
- View all your wins on hard difficulty
- See all games Player A participated in
- Find all losses regardless of player
- Combine any filters simultaneously

---

## 🔧 Configuration

### User Data Location

**User Credentials**:
- Location: `~/.minesweeper/UserData.json`
- Format: JSON array of user objects
- Contains: username, password, securityQuestion, securityAnswer

**User Question Banks**:
- Location: `~/.minesweeper/questions_{username}.csv`
- Format: Standard CSV (8 columns)
- Columns: ID, Question, Difficulty, A, B, C, D, Correct Answer

**Game History**:
- Location: `src/minesweeper/Data/history.json`
- Format: JSON array of game records
- Includes: timestamp, players, difficulty, score, result

### Audio Configuration

**Audio Resources**:
- SFX Location: `/assets/audio/sfx/`
- Music Location: `/assets/audio/music/`
- Format: WAV files
- Loaded from classpath (JAR-compatible)

**Supported Audio**:
- `boom.wav` - Mine explosion
- `click.wav` - Button clicks
- `win.wav` - Victory music
- `lose.wav` - Defeat music

### CSV Question Format

```csv
ID,Question,Difficulty,A,B,C,D,Correct Answer
1,What is MVC?,2,Model View,Controller,Pattern,All of the above,D
2,What is encapsulation?,1,Hiding data,Inheritance,Polymorphism,Abstraction,A
3,What is polymorphism?,3,Many forms,One form,No forms,Two forms,A
```

**Field Requirements**:
- **ID**: Unique integer
- **Question**: Text (max 200 characters)
- **Difficulty**: 1=Easy, 2=Medium, 3=Hard
- **A, B, C, D**: Answer options
- **Correct Answer**: Must be A, B, C, or D

---

## 🧪 Testing

### Iteration 3 Testing Strategy

#### Authentication Testing
- **TC-79 to TC-88**: Login, registration, password recovery
- **Validation**: Username uniqueness, password matching, security answers
- **Edge Cases**: Empty fields, special characters, SQL injection attempts
- **Security**: Password visibility, session management

#### User-Specific Question Banks Testing
- **TC-89 to TC-96**: CSV upload, isolation, CRUD operations
- **Validation**: User separation, file naming, data persistence
- **Edge Cases**: Concurrent user uploads, file permissions

#### History Filtering Testing (Strategy Pattern)
- **TC-97 to TC-106**: All filter combinations, sort operations
- **Validation**: Filter accuracy, combined filters, sort correctness
- **Edge Cases**: Empty history, single user, all same difficulty

#### Turn Mechanics Testing
- **TC-107 to TC-112**: Turn switching, flagging behavior
- **Validation**: Turn indicator, flag persistence, score updates
- **Edge Cases**: Rapid clicking, flag spam, cascade during opponent turn

#### Audio Testing
- **TC-113 to TC-115**: SFX playback, music transitions
- **Validation**: Audio timing, volume levels, resource cleanup
- **Edge Cases**: Missing audio files, multiple simultaneous sounds

### Test Execution Results

**Iteration 3 Test Coverage**:
- Total Test Cases: 40 essential tests
- Authentication: 10/10 passed ✅
- User Question Banks: 8/8 passed ✅
- History Filtering: 10/10 passed ✅
- Turn Mechanics: 6/6 passed ✅
- Audio Features: 3/3 passed ✅
- EXIT Functionality: 3/3 passed ✅

**Overall Status**: 40/40 tests passed (100%) ✅

---

## 🐛 Troubleshooting

### Authentication Issues

**Problem**: "User not found" on login
- **Solution**: Verify username spelling, create account if needed
- **Check**: `~/.minesweeper/UserData.json` exists and is valid JSON

**Problem**: "Passwords don't match" on registration
- **Solution**: Ensure both password fields have identical input
- **Check**: Password visibility toggle to verify typing

**Problem**: Password recovery fails
- **Solution**: Verify security answer exactly matches what you entered
- **Check**: Security answers are case-sensitive

### User Question Bank Issues

**Problem**: Questions not appearing in game
- **Solution**: Ensure you've uploaded CSV as the logged-in user
- **Check**: File exists at `~/.minesweeper/questions_{your_username}.csv`

**Problem**: Questions showing from wrong user
- **Solution**: Log out and log back in with correct user
- **Check**: `SessionContext.currentUser` is correct

**Problem**: CSV upload fails
- **Solution**: Verify CSV format matches template (8 columns)
- **Check**: No special characters or line breaks in question text

### History Filtering Issues

**Problem**: Filters not working
- **Solution**: Click "REFRESH" to reload history data
- **Check**: History file contains games with matching criteria

**Problem**: Sort button not cycling
- **Solution**: Click button multiple times to cycle through modes
- **Modes**: JSON order → Score ASC → Score DESC → repeat

**Problem**: Combined filters show no results
- **Solution**: Try fewer filters or check if games matching all criteria exist
- **Check**: History contains data meeting all filter conditions

### Audio Issues

**Problem**: No sound playing
- **Solution**: Verify audio files exist in `/assets/audio/` directories
- **Check**: JAR file includes audio resources
- **Fallback**: Application runs without audio if files missing

**Problem**: Audio plays but cuts off
- **Solution**: Ensure AudioManager.shutdown() is called on exit
- **Check**: No multiple AudioManager instances created

### Turn Mechanics Issues

**Problem**: Flagging switches turn
- **Solution**: Verify you're right-clicking (not left-clicking)
- **Expected**: Right-click flags without turn switch

**Problem**: Turn indicator not updating
- **Solution**: Refresh UI, click a cell to trigger update
- **Check**: GameController.switchTurn() called after reveals only

### General Issues

**Problem**: Application won't start
- **Solution**: 
  1. Verify JDK 17+ installed
  2. Check all dependencies present
  3. Clear `.minesweeper` directory and restart

**Problem**: JAR file crashes on launch
- **Solution**: Run with console to see error messages
  ```bash
  java -jar Mine_Sweeper_Tiger.jar
  ```

**Problem**: UI elements overlapping
- **Solution**: Increase window size or adjust screen resolution

---

## 📊 Project Statistics

### Iteration 3 Development Metrics

**Lines of Code**:
- Model Layer: ~800 lines (including Strategy classes)
- View Layer: ~1,200 lines (including Login/History enhancements)
- Controller Layer: ~500 lines (including LoginController, AudioBinder)
- Test Suite: ~300 lines
- **Total**: ~2,800 lines

**Components Created (Iteration 3)**:
- Classes: 15 new classes
- Interfaces: 2 new interfaces (Strategy pattern)
- Views: 1 new view (LoginView)
- Controllers: 1 new controller (LoginController)
- Utilities: 1 new utility (AudioBinder)

**Files and Assets**:
- Java Files: 45+ total
- CSV Files: 1 template + per-user files
- JSON Files: 2 (UserData, history)
- Audio Files: 4 (boom, click, win, lose)
- Image Assets: 1 (eye.png)

**Design Patterns Implemented**:
- Strategy Pattern (History Filtering) ✅
- Singleton Pattern (AudioManager) ✅
- Factory Pattern (UI Components) ✅
- Observer Pattern (Game State) ✅
- MVC Pattern (Architecture) ✅

---

## 👥 Team Collaboration

### Iteration 3 Roles

**Group**: Tiger  
**Course**: הנדסת תוכנה ואבטחת איכות 26 3610 א01  
**Institution**: Information Systems BSc Program  
**Semester**: סמסטר א'תשפ"ו  
**Iteration**: 3 (Final)  
**Version**: 3.0

**Team Structure**:
- **Product Owner**: Yotam
- **Scrum Master**: Mor
- **Developers**: Yotam, Mor, Juliana, Avi, Osman

### User Story Ownership (Iteration 3)

Based on actual implementation (may differ from initial plan):

- **Authentication System** (US-11 adapted): Yotam
  - Login/Registration/Password Recovery
  - User data persistence
  - Session management

- **User-Specific Question Banks** (US-12 adapted): Yotam
  - Per-user CSV files
  - Question Wizard enhancements
  - User-scoped CRUD operations

- **History Filtering - Strategy Pattern** (US-14): Mor
  - Filter strategies implementation
  - Sort strategies
  - Combined filtering logic

- **Turn Mechanics Refinement**: Avi
  - Flagging turn behavior
  - Turn indicator updates
  - Game flow consistency

- **Audio Features** (US-15 adapted): Osman
  - AudioManager implementation
  - SFX integration
  - Music playback

- **EXIT Button & Polish**: Juliana
  - EXIT functionality
  - Resource cleanup
  - Final UI polish

---

## 📚 Additional Resources

### Project Documents

- **SRS Document**: Software Requirements Specification (v3.1)
- **SDP Document**: Software Development Plan (v9.1)
- **Test Documentation**: Implementation and Testing Documentation (Iteration 3)
  - 40 essential test cases
  - Strategy pattern validation
  - Authentication security tests
- **Class Diagram**: UML class structure (updated for Iteration 3)
- **Lecture Materials**: Course reference materials
- **GitHub Repository**: https://github.com/julianaabushkara/Mine_Sweeper_Tiger
- **Drive Link**: https://drive.google.com/drive/folders/11cKiURO1uhlBGhzW74zHev8tugCUrmYD?usp=sharing

### Testing Resources

- **White Box Test Cases**: Strategy pattern coverage analysis
- **Black Box Test Cases**: Authentication flow testing
- **JUnit Test Suite**: Automated regression tests
- **Test Results**: 100% essential test coverage achieved

### Learning Resources

- **Strategy Pattern**: https://refactoring.guru/design-patterns/strategy
- **Singleton Pattern**: https://refactoring.guru/design-patterns/singleton
- **Java Sound API**: https://docs.oracle.com/javase/tutorial/sound/
- **User Authentication Best Practices**: https://owasp.org/www-project-authentication-cheat-sheet/
- **Java File I/O**: https://docs.oracle.com/javase/tutorial/essential/io/

---

## 📝 Change Log

### Version 3.0 (Iteration 3) - January 11, 2025
- ✅ **Authentication System**: Complete login/registration/password recovery
- ✅ **User-Specific Question Banks**: Per-user CSV files with isolation
- ✅ **History Filtering**: Strategy pattern with multiple filter types
- ✅ **Advanced Sorting**: Score-based ascending/descending/chronological
- ✅ **Turn Mechanics Refinement**: Flagging no longer switches turns
- ✅ **Audio Features**: SFX and background music integration
- ✅ **AudioManager**: Singleton pattern for centralized audio control
- ✅ **EXIT Button**: Proper application termination with cleanup
- ✅ **User Session Management**: SessionContext for logged-in user
- ✅ **Enhanced UI Components**: Neon combo boxes, styled dropdowns
- ✅ **Resource Management**: Classpath-based loading for JAR compatibility
- ✅ **Security**: Password visibility toggle, security questions
- ✅ **Comprehensive Testing**: 40 essential test cases with 100% pass rate

### Version 2.0 (Iteration 2) - December 8, 2024
- ✅ **US-06**: Empty cell cascade reveal mechanism
- ✅ **US-07**: Question overlay dialog with answer integration
- ✅ **US-08**: Complete scoring and lives system with HUD
- ✅ **US-09**: End-game detection and history persistence
- ✅ **US-10**: Enhanced history view with detailed statistics
- ✅ Comprehensive testing suite (white box, black box, JUnit)
- ✅ Real-time HUD with color-coded feedback
- ✅ Automatic game history saving
- ✅ Win/loss detection and board revelation

### Version 1.0 (Iteration 1) - November 30, 2024
- ✅ Start menu navigation
- ✅ Question Wizard with CSV upload
- ✅ New Game setup screen
- ✅ Game board framework
- ✅ History view framework
- ✅ Dark sci-fi theme
- ✅ Custom UI components
- ✅ MVC architecture
- ✅ Navigation system

---

## 🎯 Iteration 3 Achievements

### Technical Achievements
- ✅ Implemented Strategy pattern for extensible filtering
- ✅ Created Singleton AudioManager with resource caching
- ✅ Built user authentication system with security features
- ✅ Developed per-user question bank isolation
- ✅ Refined turn mechanics for better gameplay flow
- ✅ Integrated audio feedback throughout application
- ✅ Achieved 100% test coverage for essential functionality

### Design Pattern Mastery
- ✅ Strategy Pattern: Clean filtering architecture
- ✅ Singleton Pattern: Centralized audio management
- ✅ Factory Pattern: UI component creation
- ✅ Observer Pattern: State change notifications
- ✅ MVC Pattern: Consistent architectural separation

### Team Achievements
- ✅ Completed all Iteration 3 enhancements on time
- ✅ Delivered fully functional authentication system
- ✅ Implemented sophisticated filtering with multiple strategies
- ✅ Created polished, professional final product
- ✅ Achieved comprehensive documentation and testing
- ✅ Successfully demonstrated all design patterns

### Quality Achievements
- ✅ Zero critical bugs in production
- ✅ All acceptance criteria exceeded
- ✅ Successful final sprint review and demo
- ✅ Positive feedback from instructors and peers
- ✅ Professional-grade code quality
- ✅ Complete feature set delivered

---

## 🔮 Future Enhancement Ideas

### Potential Post-Iteration 3 Features

**Advanced User Management**:
- User profiles with avatars
- Player statistics and rankings
- Achievement system
- Friend lists and social features

**Enhanced Audio**:
- Adjustable volume controls
- Music playlist selection
- Custom sound effects
- Background ambient sounds

**Improved History**:
- Export history to PDF/CSV
- Graphical statistics (charts, graphs)
- Time-based filtering (date ranges)
- Player comparison views

**Gameplay Enhancements**:
- Custom board sizes
- Power-ups and special abilities
- Timed challenges
- Daily/weekly challenges
- Replay functionality

**Technical Improvements**:
- Online multiplayer support
- Cloud save synchronization
- Mobile app version
- Theme customization
- Internationalization (multiple languages)

**Analytics**:
- Performance analytics dashboard
- Question difficulty analysis
- Player behavior insights
- Win rate calculations

---

## 📜 License

This project is developed as part of an academic course.  
All rights reserved to Group Tiger and the academic institution.

---

## 🙏 Acknowledgments

- **Course Instructors**: George and Ali for guidance, requirements, and comprehensive feedback throughout all iterations
- **Teaching Assistants**: For support during development, testing, and design pattern implementation
- **Java Community**: For Swing best practices, audio integration patterns, and design pattern examples
- **Team Members**: For exceptional collaboration, dedication, and professional teamwork across three complete iterations
- **Beta Testers**: For valuable feedback during all development phases
- **Open Source Contributors**: For audio libraries and UI component inspiration
- **Tiger Conservation Supporters**: For ongoing inspiration and motivation

---

## 📧 Contact & Support

For questions, issues, or contributions:
- Contact course instructors: George and Ali
- Consult teaching assistants
- Review project documentation in Drive
- Check troubleshooting guide above
- Submit issues on GitHub repository

**GitHub**: https://github.com/julianaabushkara/Mine_Sweeper_Tiger  
**Drive**: https://drive.google.com/drive/folders/11cKiURO1uhlBGhzW74zHev8tugCUrmYD?usp=sharing

---

## 🎊 Final Project Summary

### Three-Iteration Journey

**Iteration 1**: Foundation and Framework
- Established MVC architecture
- Created navigation system
- Built Question Wizard (read-only)
- Designed dark sci-fi UI theme
- Implemented basic board generation

**Iteration 2**: Gameplay and Mechanics
- Full gameplay implementation
- Question overlay system
- Scoring and lives mechanics
- End-game conditions
- History persistence
- Empty cell cascade

**Iteration 3**: Polish and Professional Features
- User authentication system
- Personalized question banks
- Advanced filtering (Strategy pattern)
- Audio feedback integration
- Turn mechanics refinement
- Final polish and bug fixes

### Final Metrics

**Total Development Time**: 3 iterations (~8 weeks)  
**Total Lines of Code**: ~6,500 lines  
**Total Test Cases**: 40 essential + comprehensive test suite  
**Design Patterns Implemented**: 5 major patterns  
**Team Members**: 5 dedicated developers  
**Features Delivered**: 15 user stories across 3 iterations  
**Final Status**: Complete and production-ready ✅

---

**Built with ❤️ by Group Tiger**

**Version**: 3.0.0 (FINAL)  
**Last Updated**: January 2025  
**Status**: Project Complete ✅ | Ready for Deployment 🚀

---

*This README provides comprehensive documentation for the Minesweeper Quiz Game - Iteration 3 (Final). For previous iterations, see README_Iteration_1 and README_Iteration_2. This represents the culmination of three complete development iterations demonstrating professional software engineering practices, comprehensive testing methodologies, and industry-standard design pattern implementation.*

**Final Milestone**: M3 Complete - Full authentication, personalized experiences, Strategy pattern filtering, audio integration, and professional polish ✅

---

**Thank you for an incredible learning experience! 🐯**
