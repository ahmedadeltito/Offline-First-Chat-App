# Offline-First Chat App - Architecture Documentation

## Overview

This document provides a comprehensive overview of the architecture for the Offline-First Chat App, a demonstration of modern Android development practices using Clean Architecture, Jetpack Compose, WorkManager, and Room.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Project Structure](#project-structure)
3. [Clean Architecture Layers](#clean-architecture-layers)
4. [Key Technologies](#key-technologies)
5. [Data Flow](#data-flow)
6. [Component Details](#component-details)
7. [Design Patterns](#design-patterns)
8. [Sync System Deep Dive](#sync-system-deep-dive)
9. [Testing Strategy](#testing-strategy)
10. [Future Enhancements](#future-enhancements)

## Architecture Overview

The app follows **Clean Architecture** principles with a clear separation of concerns across multiple layers:

```
┌───────────────────────────────────────────────────────────┐
│                    Presentation Layer                     │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────┐   │
│  │   ChatRoute │  │ ChatScreen  │  │   UI Components  │   │
│  └─────────────┘  └─────────────┘  └──────────────────┘   │
└───────────────────────────────────────────────────────────┘
┌───────────────────────────────────────────────────────────┐
│                     Domain Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────┐   │
│  │   Use Cases │  │  Repository │  │   Domain Models  │   │
│  └─────────────┘  └─────────────┘  └──────────────────┘   │
└───────────────────────────────────────────────────────────┘
┌───────────────────────────────────────────────────────────┐
│                      Data Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────┐   │
│  │   Local DB  │  │ Remote API  │  │   Sync Manager   │   │
│  └─────────────┘  └─────────────┘  └──────────────────┘   │
└───────────────────────────────────────────────────────────┘
```

### Key Architectural Principles

1. **Dependency Inversion**: High-level modules don't depend on low-level modules
2. **Single Responsibility**: Each class has one reason to change
3. **Open/Closed Principle**: Open for extension, closed for modification
4. **Unidirectional Data Flow**: State flows in one direction through the app

## Project Structure

```
app/src/main/java/com/ahmedadeltito/chatapp/
├── data/                           # Data Layer
│   ├── local/                      # Local database (Room)
│   │   ├── AppDatabase.kt
│   │   ├── MessageDao.kt
│   │   ├── MessageEntity.kt
│   │   ├── DateConverter.kt
│   │   └── MessageStatusConverter.kt
│   ├── remote/                     # Remote API
│   │   ├── ChatApiService.kt
│   │   └── MessageDto.kt
│   ├── sync/                       # Background sync
│   │   ├── SyncManager.kt
│   │   └── SyncWorker.kt
│   └── ChatRepositoryImpl.kt       # Repository implementation
├── domain/                         # Domain Layer
│   ├── Message.kt                  # Domain model
│   ├── ChatRepository.kt           # Repository interface
│   └── usecase/                    # Use cases
│       ├── SendMessageUseCase.kt
│       ├── RetryMessageUseCase.kt
│       ├── ToggleSyncUseCase.kt
│       ├── SyncStatusUseCase.kt
│       ├── ObserveMessagesUseCase.kt
│       ├── RefreshMessagesUseCase.kt
│       └── InitializeSyncUseCase.kt
├── presentation/                    # Presentation Layer
│   └── chat/
│       ├── ChatScreenContract.kt   # UDF contract
│       ├── ChatUiModels.kt         # UI models with @Stable
│       ├── ChatViewModel.kt        # ViewModel
│       ├── ChatScreen.kt           # UI component
│       └── ChatRoute.kt            # Route/Container
├── ui/                             # UI Components
│   ├── component/                  # Reusable components
│   │   ├── MessageBubble.kt
│   │   ├── MessageInputBar.kt
│   │   ├── SyncStatusCard.kt
│   │   ├── EducationalHeader.kt
│   │   ├── GenericErrorScreen.kt
│   │   ├── DisabledMessageInputBar.kt
│   │   ├── TopAppBarTitle.kt
│   │   └── TopSnackbarHost.kt
│   └── theme/                      # UI theming
└── di/                             # Dependency Injection
    ├── DatabaseModule.kt
    ├── NetworkModule.kt
    ├── RepositoryModule.kt
    ├── UseCaseModule.kt
    └── AppModule.kt
```

## Clean Architecture Layers

### 1. Presentation Layer

The presentation layer is responsible for displaying data to the user and handling user interactions.

#### Key Components:

- **ChatRoute**: Container component that manages ViewModel creation and side effect handling
- **ChatScreen**: Pure UI component focused on rendering state
- **ChatViewModel**: Manages UI state and business logic
- **ChatScreenContract**: Defines the Unidirectional Data Flow contract
- **ChatUiModels**: UI-specific models with @Stable annotations for Compose optimization

#### UDF Pattern Implementation:

```kotlin
sealed interface ChatUiState {
    data class Loading(val currentUserId: String) : ChatUiState
    data class Success(val messages: List<ChatMessageUiModel>, val currentUserId: String) : ChatUiState
    data class Error(val message: String, val currentUserId: String) : ChatUiState
}

sealed interface ChatUiEvent {
    data class InputChanged(val newInput: String) : ChatUiEvent
    object SendClicked : ChatUiEvent
    data class RetryMessageClicked(val messageId: String) : ChatUiEvent
    object SyncToggleClicked : ChatUiEvent
    object RefreshClicked : ChatUiEvent
    object ClearInputClicked : ChatUiEvent
}

sealed interface ChatSideEffect {
    data class ShowSnackbar(val message: String) : ChatSideEffect
    object NavigateToSettings : ChatSideEffect
    object NavigateToProfile : ChatSideEffect
    object NavigateToChatDetails : ChatSideEffect
    data class ShareMessage(val messageText: String) : ChatSideEffect
}
```

### 2. Domain Layer

The domain layer contains the business logic and is independent of external frameworks.

#### Key Components:

- **Message**: Domain model representing a chat message
- **ChatRepository**: Interface defining data operations
- **Use Cases**: Encapsulate business logic and orchestrate data operations

#### Use Cases:

1. **SendMessageUseCase**: Handles message sending with sync status
2. **RetryMessageUseCase**: Retries failed message sending
3. **ToggleSyncUseCase**: Manages sync enable/disable
4. **SyncStatusUseCase**: Observes and manages sync status
5. **ObserveMessagesUseCase**: Observes message updates
6. **RefreshMessagesUseCase**: Refreshes messages from remote
7. **InitializeSyncUseCase**: Initializes the sync system

### 3. Data Layer

The data layer handles data operations and external dependencies.

#### Local Storage (Room):

```kotlin
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Date,
    val status: MessageStatus
)
```

#### Remote API:

```kotlin
interface ChatApiService {
    @GET("messages")
    suspend fun getMessages(): List<MessageDto>
    
    @POST("messages")
    suspend fun sendMessage(@Body message: MessageDto): MessageDto
}
```

#### Sync Management:

- **SyncManager**: Coordinates background sync operations
- **SyncWorker**: WorkManager worker for background sync
- **WorkManager**: Handles periodic sync tasks

## Key Technologies

### Core Technologies

1. **Jetpack Compose**: Modern declarative UI toolkit
2. **Room**: Local database for offline storage
3. **WorkManager**: Background task scheduling
4. **Hilt**: Dependency injection
5. **Kotlin Coroutines**: Asynchronous programming
6. **StateFlow**: Reactive state management

### Architecture Patterns

1. **Clean Architecture**: Separation of concerns
2. **MVVM**: Model-View-ViewModel pattern
3. **Repository Pattern**: Data access abstraction
4. **Use Case Pattern**: Business logic encapsulation
5. **Unidirectional Data Flow**: Predictable state management

## Data Flow

### Message Sending Flow

```
User Input → ChatScreen → ChatViewModel → SendMessageUseCase → ChatRepository → 
Local DB (immediate) → Remote API (background) → SyncManager → WorkManager
```

### Message Receiving Flow

```
Remote API → ChatRepository → Local DB → ObserveMessagesUseCase → 
ChatViewModel → ChatScreen → UI Update
```

### Sync Flow

```
WorkManager → SyncWorker → SyncManager → ChatRepository → 
Local DB ↔ Remote API → UI State Update
```

## Component Details

### ChatViewModel

The ViewModel manages UI state and coordinates between use cases:

```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    // ... other use cases
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading())
    private val _statusState = MutableStateFlow<ChatUiStatus>(ChatUiStatus())
    
    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendClicked -> handleSendClicked()
            // ... other event handlers
        }
    }
}
```

### Use Cases

Use cases encapsulate business logic and are easily testable:

```kotlin
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val syncManager: SyncManager
) {
    operator fun invoke(
        text: String,
        senderId: String,
        syncEnabled: Boolean
    ): SendMessageResult {
        // Business logic implementation
    }
}
```

### Repository Pattern

The repository provides a clean abstraction over data sources:

```kotlin
interface ChatRepository {
    fun getMessages(): Flow<List<Message>>
    suspend fun sendMessage(text: String, senderId: String): Result<Message>
    suspend fun fetchAndSyncRemoteMessages()
}
```

## Design Patterns

### 1. Repository Pattern

Provides a clean abstraction over data sources, allowing the domain layer to be independent of data implementation details.

### 2. Use Case Pattern

Encapsulates business logic in single-purpose classes, making the code more testable and maintainable.

### 3. Unidirectional Data Flow

State flows in one direction: UI Events → ViewModel → Use Cases → Repository → State Updates → UI.

### 4. Dependency Injection

Uses Hilt for dependency injection, making components loosely coupled and easily testable.

### 5. Observer Pattern

Uses StateFlow to observe state changes and update the UI reactively.

## Sync System Deep Dive

The sync system is the heart of the offline-first architecture, ensuring data consistency between local storage and remote servers while providing a seamless user experience.

### Architecture Overview

```
┌────────────────────────────────────────────────────────────┐
│                          UI Layer                          │
│  ┌─────────────┐  ┌───────────────┐  ┌──────────────────┐  │
│  │ ChatScreen  │  │ ChatViewModel │  │  SyncStatusCard  │  │
│  └─────────────┘  └───────────────┘  └──────────────────┘  │
└────────────────────────────────────────────────────────────┘
┌────────────────────────────────────────────────────────────┐
│                       Business Logic                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │ SyncStatusUC │  │ ToggleSyncUC │  │ InitializeSyncUC │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
└────────────────────────────────────────────────────────────┘
┌───────────────────────────────────────────────────────────┐
│                       Sync Layer                          │
│  ┌──────────────┐  ┌─────────────┐  ┌──────────────────┐  │
│  │ SyncManager  │  │ SyncWorker  │  │   WorkManager    │  │
│  └──────────────┘  └─────────────┘  └──────────────────┘  │
└───────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                         Data Layer                          │
│  ┌───────────┐  ┌─────────────┐  ┌──────────────────┐       │
│  │  Room DB  │  │ Remote API  │  │   Repository     │       │
│  └───────────┘  └─────────────┘  └──────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### Sync Components

#### 1. SyncManager

The `SyncManager` is the central coordinator for all sync operations:

```kotlin
class SyncManager @Inject constructor(
    private val workManager: WorkManager
) {
    // Manages sync state and coordinates sync operations
    fun observeSyncStatus(): Flow<String>
    fun initializeSync()
    fun toggleSync(enabled: Boolean)
    fun triggerSync()
}
```

**Responsibilities:**
- Manages sync state (enabled/disabled)
- Provides sync status updates to UI
- Handles sync enable/disable operations

#### 2. SyncWorker

The `SyncWorker` is a WorkManager worker that performs background sync:

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // 1. Fetch remote messages
            // 2. Merge with local messages
            // 3. Update local database
            // 4. Handle conflicts
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

**Responsibilities:**
- Executes sync operations in background
- Handles network failures gracefully
- Implements retry logic
- Reports sync status

#### 3. WorkManager

WorkManager handles the scheduling and execution of background sync tasks:

```kotlin
// Periodic sync every 15 minutes
val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
    repeatInterval = 15,
    repeatIntervalTimeUnit = TimeUnit.MINUTES
).setConstraints(
    Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
).build()
```

### Sync Flow Diagrams

#### 1. Initial Sync Setup

```
App Launch
    ↓
InitializeSyncUseCase
    ↓
SyncManager.initializeSync()
    ↓
WorkManager.enqueuePeriodicWork()
    ↓
Sync Status: "Idle"
```

#### 2. Message Sending with Sync

```
User Sends Message
    ↓
SendMessageUseCase
    ↓
Repository.saveMessage() [Local DB - Immediate]
    ↓
UI: Message appears instantly
    ↓
if (syncEnabled) {
    SyncManager.triggerSync()
    ↓
    WorkManager.enqueueOneTimeWork()
    ↓
    SyncWorker.doWork()
    ↓
    Repository.sendToRemote()
    ↓
    Update Message Status: SENT_TO_SERVER
}
```

#### 3. Background Sync Process

```
WorkManager Triggers Periodic Sync
    ↓
SyncWorker.doWork()
    ↓
Repository.fetchAndSyncRemoteMessages()
    ↓
┌──────────────────────────────────────┐
│           Sync Process               │
│  ┌──────────────┐   ┌─────────────┐  │
│  │ Fetch Remote │   │ Merge Local │  │
│  │   Messages   │   │   & Remote  │  │
│  └──────────────┘   └─────────────┘  │
│  ┌──────────────┐   ┌─────────────┐  │
│  │ Handle       │   │ Update      │  │
│  │ Conflicts    │   │ Local DB    │  │
│  └──────────────┘   └─────────────┘  │
└──────────────────────────────────────┘
    ↓
Update Sync Status
    ↓
UI Updates via StateFlow
```

#### 4. Sync State Management

```
┌────────────────────────────────────┐
│             Sync States            │
│  ┌─────────────┐  ┌─────────────┐  │
│  │   IDLE      │  │  SYNCING    │  │
│  │             │  │             │  │
│  │ "Last synced│  │ "Syncing..."│  │
│  │  X min ago" │  │             │  │
│  └─────────────┘  └─────────────┘  │
│                                    │
│  ┌─────────────┐  ┌─────────────┐  │
│  │   ERROR     │  │  DISABLED   │  │
│  │             │  │             │  │
│  │ "Sync failed│  │ "Sync off"  │  │
│  │  - retry"   │  │             │  │
│  └─────────────┘  └─────────────┘  │
└────────────────────────────────────┘
```

### Conflict Resolution Strategy

The app implements a **"Last Write Wins"** strategy with timestamp-based conflict resolution:

```kotlin
fun resolveMessageConflicts(
    localMessages: List<Message>,
    remoteMessages: List<Message>
): List<Message> {
    return (localMessages + remoteMessages)
        .groupBy { it.id }
        .mapValues { (_, messages) ->
            messages.maxByOrNull { it.timestamp }
        }
        .values
        .filterNotNull()
        .sortedBy { it.timestamp }
}
```

**Conflict Resolution Rules:**
1. **Timestamp-based**: Newer messages take precedence
2. **Status preservation**: Failed messages are preserved for retry
3. **Merge strategy**: Combine local and remote messages
4. **Deduplication**: Remove duplicate messages by ID

### Network Handling

#### Online Scenario
```
Network Available
    ↓
Sync Enabled
    ↓
Immediate local save + Background remote sync
    ↓
Real-time status updates
    ↓
Seamless user experience
```

#### Offline Scenario
```
Network Unavailable
    ↓
Sync Disabled or Pending
    ↓
Local-only operations
    ↓
Queue for later sync
    ↓
Graceful degradation
```

#### Network Recovery
```
Network Restored
    ↓
WorkManager detects connectivity
    ↓
Triggers pending sync operations
    ↓
Resolves queued messages
    ↓
Updates UI status
```

### Performance Optimizations

#### 1. Incremental Sync
- Only sync messages newer than last sync timestamp
- Reduces network usage and processing time

#### 2. Batch Operations
- Group multiple messages for single API call
- Reduces network overhead

#### 3. Smart Retry Logic
- Exponential backoff for failed syncs
- Prevents overwhelming the server

#### 4. Background Processing
- Sync operations don't block UI
- User can continue using the app

### Error Handling

#### Sync Failures
```kotlin
when (syncResult) {
    is Success -> updateStatus("Last synced: ${formatTime()}")
    is NetworkError -> updateStatus("Network error - will retry")
    is ServerError -> updateStatus("Server error - will retry")
    is UnknownError -> updateStatus("Sync failed - will retry")
}
```

#### User Feedback
- Clear status messages in UI
- Retry options for failed operations
- Visual indicators for sync state
- Educational messages about offline-first approach

### Testing Sync System

#### Unit Tests
```kotlin
@Test
fun `sync merges local and remote messages correctly`() {
    // Test conflict resolution
}

@Test
fun `sync handles network failures gracefully`() {
    // Test error scenarios
}

@Test
fun `sync respects enable/disable state`() {
    // Test sync toggle
}
```

#### Integration Tests
```kotlin
@Test
fun `complete sync flow works end-to-end`() {
    // Test full sync cycle
}
```

## Testing Strategy

### Unit Testing

- **Use Cases**: Test business logic in isolation
- **ViewModels**: Test state management and event handling
- **Repositories**: Test data operations
- **Sync Components**: Test sync logic and error handling

### UI Testing

- **Compose Preview**: Visual testing of UI components
- **Integration Tests**: Test complete user flows

### Architecture Testing

- **Dependency Rule**: Ensure layers follow Clean Architecture rules
- **Testability**: All components are easily testable

## Future Enhancements

### Planned Features

1. **Message Encryption**: End-to-end encryption
2. **Message Search**: Local and remote search
3. **Message Reactions**: Like, heart, etc.

### Technical Improvements

1. **Navigation**: Navigation Compose integration
2. **Caching**: Advanced caching strategies

## Conclusion

This architecture provides a solid foundation for a scalable, maintainable, and testable chat application. The separation of concerns, use of modern Android technologies, and adherence to Clean Architecture principles make it easy to extend and modify as requirements evolve.

The offline-first approach ensures a great user experience even with poor network connectivity, while the reactive programming model provides a responsive and fluid interface.

The sync system is particularly robust, handling various network conditions gracefully while maintaining data consistency and providing clear user feedback about sync status. 