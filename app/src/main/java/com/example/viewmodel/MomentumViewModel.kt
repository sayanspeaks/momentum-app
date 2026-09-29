package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Subtask
import com.example.data.Task
import com.example.data.TaskRepository
import com.example.service.GeminiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MomentumViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "MomentumViewModel"
    private val repository: TaskRepository
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Database flow of all tasks
    val allTasks: StateFlow<List<Task>>

    // UI state parameters
    private val _availableMinutes = MutableStateFlow(200) // 3h 20m available today
    val availableMinutes: StateFlow<Int> = _availableMinutes.asStateFlow()

    private val _isOnboarded = MutableStateFlow(false)
    val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Recommendation of next best action
    private val _nextBestAction = MutableStateFlow<NextBestAction?>(null)
    val nextBestAction: StateFlow<NextBestAction?> = _nextBestAction.asStateFlow()

    // Simulation updates
    private val _toastNotification = MutableSharedFlow<String>()
    val toastNotification: SharedFlow<String> = _toastNotification.asSharedFlow()

    // Current active task
    private val _activeTaskId = MutableStateFlow<Int?>(null)
    val activeTaskId: StateFlow<Int?> = _activeTaskId.asStateFlow()

    // Reality check proposal (rebuilt schedule)
    private val _rebuiltSchedule = MutableStateFlow<RebuiltSchedule?>(null)
    val rebuiltSchedule: StateFlow<RebuiltSchedule?> = _rebuiltSchedule.asStateFlow()

    // Rescue Plan proposal
    private val _rescuePlan = MutableStateFlow<RescuePlan?>(null)
    val rescuePlan: StateFlow<RescuePlan?> = _rescuePlan.asStateFlow()

    // Autopsy suggestion
    private val _autopsySuggestion = MutableStateFlow<AutopsySuggestion?>(null)
    val autopsySuggestion: StateFlow<AutopsySuggestion?> = _autopsySuggestion.asStateFlow()

    // Commitment Radar extraction
    private val _detectedCommitment = MutableStateFlow<CommitmentDetection?>(null)
    val detectedCommitment: StateFlow<CommitmentDetection?> = _detectedCommitment.asStateFlow()

    // Multimodal parsed tasks
    private val _parsedImageTasks = MutableStateFlow<List<Task>?>(null)
    val parsedImageTasks: StateFlow<List<Task>?> = _parsedImageTasks.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TaskRepository(database.taskDao())
        allTasks = repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Listen for task updates to refresh next move and check for first-launch status
        viewModelScope.launch {
            allTasks.collect { tasks ->
                if (tasks.isNotEmpty()) {
                    _isOnboarded.value = true
                    calculateNextAction(tasks)
                } else {
                    _isOnboarded.value = false
                }
            }
        }
    }

    // Load static demo data to pre-populate the dashboard
    fun loadDemoData() {
        viewModelScope.launch {
            _isAiThinking.value = true
            repository.clearAll()
            
            val demoTasks = listOf(
                Task(
                    title = "Finish client presentation",
                    description = "Need to compile final design updates and slide transitions.",
                    category = "MUST DO",
                    priority = "High",
                    status = "Blocked",
                    estimatedMinutes = 60,
                    deadline = "Tomorrow",
                    dependency = "Rahul's sales numbers",
                    subtasksJson = serializeSubtasks(listOf(
                        Subtask("Add sales numbers", false),
                        Subtask("Review slides", false),
                        Subtask("Send to Priya", false)
                    ))
                ),
                Task(
                    title = "Review Priya's API pull request",
                    description = "Analyze endpoint structure and performance enhancements before deployment.",
                    category = "MUST DO",
                    priority = "High",
                    status = "Pending",
                    estimatedMinutes = 25,
                    deadline = "Today",
                    subtasksJson = serializeSubtasks(listOf(
                        Subtask("Verify schema changes", false),
                        Subtask("Run integration tests", false)
                    ))
                ),
                Task(
                    title = "Book car service",
                    description = "Get brakes checked and oil changed.",
                    category = "CAN DO",
                    priority = "Low",
                    status = "Pending",
                    estimatedMinutes = 15,
                    deadline = "Friday"
                ),
                Task(
                    title = "Prepare AWS architecture",
                    description = "Review IAM policies, ECS setups, and CloudFront distributions.",
                    category = "SHOULD DO",
                    priority = "Medium",
                    status = "Pending",
                    estimatedMinutes = 90,
                    deadline = "Thursday",
                    postponedCount = 4
                ),
                Task(
                    title = "Send database report",
                    description = "Compile query latency metrics and cache hits.",
                    category = "MUST DO",
                    priority = "Medium",
                    status = "Pending",
                    estimatedMinutes = 20,
                    deadline = "Today"
                ),
                Task(
                    title = "Complete AWS certification study",
                    description = "Practice quiz and serverless scaling deep dive.",
                    category = "SHOULD DO",
                    priority = "Medium",
                    status = "Pending",
                    estimatedMinutes = 45,
                    deadline = "Sunday"
                ),
                Task(
                    title = "Update portfolio",
                    description = "Add Case studies for past three major projects.",
                    category = "CAN DO",
                    priority = "Low",
                    status = "Pending",
                    estimatedMinutes = 60,
                    deadline = "Next week"
                )
            )
            repository.insertAll(demoTasks)
            _toastNotification.emit("Predefined demo tasks loaded successfully!")
            _isOnboarded.value = true
            _isAiThinking.value = false
        }
    }

    // Set custom available minutes
    fun setAvailableMinutes(minutes: Int) {
        _availableMinutes.value = minutes
        viewModelScope.launch {
            calculateNextAction(allTasks.value)
        }
    }

    // Start a task
    fun startTask(taskId: Int) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                _activeTaskId.value = taskId
                repository.update(task.copy(status = "Started"))
                _toastNotification.emit("Started task: \"${task.title}\"")
            }
        }
    }

    // Complete a task
    fun completeTask(taskId: Int) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                if (_activeTaskId.value == taskId) {
                    _activeTaskId.value = null
                }
                repository.update(task.copy(status = "Completed", actualMinutes = task.estimatedMinutes))
                _toastNotification.emit("Completed: \"${task.title}\"! Great momentum!")
            }
        }
    }

    // Postpone a task
    fun postponeTask(taskId: Int, choice: PostponeChoice) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            if (task != null) {
                val nextDeadline = when (choice) {
                    PostponeChoice.TOMORROW -> "Tomorrow"
                    PostponeChoice.WEDNESDAY -> "Wednesday"
                    PostponeChoice.SPLIT -> "Split session"
                }
                repository.update(task.copy(
                    deadline = nextDeadline,
                    postponedCount = task.postponedCount + 1
                ))
                _toastNotification.emit("Intelligently postponed \"${task.title}\" to $nextDeadline")
            }
        }
    }

    // Delete a task
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            if (_activeTaskId.value == taskId) {
                _activeTaskId.value = null
            }
            repository.deleteById(taskId)
            _toastNotification.emit("Task deleted.")
        }
    }

    // Reset database
    fun resetData() {
        viewModelScope.launch {
            repository.clearAll()
            _isOnboarded.value = false
            _activeTaskId.value = null
            _nextBestAction.value = null
            _rebuiltSchedule.value = null
            _rescuePlan.value = null
            _autopsySuggestion.value = null
            _detectedCommitment.value = null
        }
    }

    // Toggle subtask completion
    fun toggleSubtask(task: Task, subtaskIndex: Int) {
        viewModelScope.launch {
            val subtasks = task.getSubtasks().toMutableList()
            if (subtaskIndex in subtasks.indices) {
                val sub = subtasks[subtaskIndex]
                subtasks[subtaskIndex] = sub.copy(isCompleted = !sub.isCompleted)
                val updatedTask = task.copy(subtasksJson = serializeSubtasks(subtasks))
                repository.update(updatedTask)
            }
        }
    }

    // SIMULATE UPDATE: Rahul sends sales numbers -> unlocks Blocked task
    fun simulateUpdate() {
        viewModelScope.launch {
            _isAiThinking.value = true
            val tasks = allTasks.value
            val blockedTask = tasks.find { it.title.contains("presentation", ignoreCase = true) && it.status == "Blocked" }
            
            if (blockedTask != null) {
                // Change status from Blocked to Pending (READY)
                val updated = blockedTask.copy(
                    status = "Pending",
                    dependency = null, // dependency resolved
                    notes = "Sales numbers received from Rahul."
                )
                repository.update(updated)
                _toastNotification.emit("🔔 Simulation: Rahul sent the sales numbers!")
            } else {
                _toastNotification.emit("🔔 Presentation task is already active or completed!")
            }
            _isAiThinking.value = false
        }
    }

    // --- NEXT BEST ACTION CALCULATION ---
    
    fun calculateNextAction(tasks: List<Task>) {
        val pendingTasks = tasks.filter { it.status != "Completed" }
        if (pendingTasks.isEmpty()) {
            _nextBestAction.value = null
            return
        }

        viewModelScope.launch {
            _isAiThinking.value = true
            
            // Format tasks list for Gemini prompting
            val taskListStr = pendingTasks.joinToString("\n") { 
                "- [ID: ${it.id}] ${it.title} (${it.estimatedMinutes} min), Category: ${it.category}, Priority: ${it.priority}, Status: ${it.status}, Deadline: ${it.deadline}, Blocker: ${it.dependency ?: "None"}"
            }

            val systemPrompt = "You are Momentum, an intelligent task execution assistant. Your job is to select the exact next single best action for the user to work on right now."
            val prompt = """
                Based on the user's current tasks and context, select the absolute single BEST NEXT action to perform right now.
                
                Current Time: 10:00 AM
                Available Minutes: ${availableMinutes.value} minutes
                
                Tasks list:
                $taskListStr
                
                Choose a task that is NOT Completed and NOT Blocked.
                Prioritize:
                1. High urgency / due today
                2. Unblocks other work
                3. High impact
                4. Can realistically fit inside available minutes (${availableMinutes.value} mins)
                5. High priority
                
                Return exactly a JSON object matching this schema:
                {
                   "taskId": Int,
                   "recommendation": "Task title",
                   "reasoning": ["First short reason", "Second short reason", "Third short reason"],
                   "estimatedMinutes": Int,
                   "urgency": "High" or "Medium" or "Low",
                   "impact": "High impact" or "Medium impact" or "Low impact",
                   "confidence": Int
                }
                Do not include any extra text or backticks around JSON.
            """.trimIndent()

            val aiResult = GeminiClient.generate(prompt, systemPrompt, responseJson = true)
            var actionParsed: NextBestAction? = null

            if (aiResult != null) {
                try {
                    val jsonObj = JSONObject(aiResult)
                    val taskId = jsonObj.getInt("taskId")
                    // Check if the recommended task actually exists and is not blocked
                    val task = tasks.find { it.id == taskId }
                    if (task != null && task.status != "Completed" && task.status != "Blocked") {
                        val reasoningArr = jsonObj.getJSONArray("reasoning")
                        val reasoning = List(reasoningArr.length()) { reasoningArr.getString(it) }
                        actionParsed = NextBestAction(
                            taskId = taskId,
                            recommendation = jsonObj.getString("recommendation"),
                            reasoning = reasoning,
                            estimatedMinutes = jsonObj.getInt("estimatedMinutes"),
                            urgency = jsonObj.getString("urgency"),
                            impact = jsonObj.getString("impact"),
                            confidence = jsonObj.getInt("confidence")
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse next action JSON from AI", e)
                }
            }

            // Fallback algorithm if AI failed or suggested a blocked/non-existent task
            if (actionParsed == null) {
                actionParsed = getFallbackNextAction(pendingTasks)
            }

            _nextBestAction.value = actionParsed
            _isAiThinking.value = false
        }
    }

    private fun getFallbackNextAction(pendingTasks: List<Task>): NextBestAction {
        // filter out blocked ones
        val executable = pendingTasks.filter { it.status != "Blocked" }
        if (executable.isEmpty()) {
            // if all are blocked, select any first pending task
            val task = pendingTasks.first()
            return NextBestAction(
                taskId = task.id,
                recommendation = task.title,
                reasoning = listOf("Needs dependencies resolved, but holds high overall value.", "No fully ready actions available."),
                estimatedMinutes = task.estimatedMinutes,
                urgency = "Medium",
                impact = "Medium impact",
                confidence = 50
            )
        }

        // Sort by priority (High -> Medium -> Low), and deadline ("Today" first)
        val sorted = executable.sortedWith(compareByDescending<Task> { it.priority == "High" }
            .thenByDescending { it.deadline == "Today" }
            .thenBy { it.estimatedMinutes })

        val task = sorted.first()
        val reasons = mutableListOf<String>()
        if (task.deadline == "Today") reasons.add("Due today")
        if (task.priority == "High") reasons.add("High impact and priority")
        reasons.add("Fits comfortably into your ${availableMinutes.value}m window (${task.estimatedMinutes} mins)")
        if (task.title.contains("API", ignoreCase = true)) reasons.add("Blocks deployment workflow")

        return NextBestAction(
            taskId = task.id,
            recommendation = task.title,
            reasoning = reasons,
            estimatedMinutes = task.estimatedMinutes,
            urgency = if (task.deadline == "Today") "High" else "Medium",
            impact = if (task.priority == "High") "High impact" else "Medium impact",
            confidence = 88
        )
    }

    // --- NATURAL LANGUAGE TASK CREATION ---
    
    fun createWithNaturalLanguage(inputText: String) {
        if (inputText.isBlank()) return
        
        viewModelScope.launch {
            _isAiThinking.value = true
            _toastNotification.emit("Analyzing commitment with Momentum AI...")

            val systemPrompt = "You are Momentum task extraction engine. Extract tasks and structural values from natural language user instructions."
            val prompt = """
                Convert the following natural language task statement into a structured JSON task object.
                User prompt: "$inputText"
                
                The schema MUST match:
                {
                   "title": "Clear action title",
                   "description": "Brief context description",
                   "deadline": "Today" or "Tomorrow" or "Thursday" or specific day,
                   "category": "MUST DO" or "SHOULD DO" or "CAN DO" or "WAITING FOR",
                   "priority": "High" or "Medium" or "Low",
                   "estimatedMinutes": Int,
                   "dependency": "Person name or task details if blocked/waiting",
                   "status": "Blocked" or "Pending",
                   "subtasks": ["Subtask action 1", "Subtask action 2", "Subtask action 3"]
                }
                
                Be smart. If they say "waiting for Rahul to send numbers", the status is "Blocked" and dependency is "Waiting for Rahul -> sales numbers".
                Return only raw JSON. Do not include any extra text.
            """.trimIndent()

            val aiResult = GeminiClient.generate(prompt, systemPrompt, responseJson = true)
            var success = false

            if (aiResult != null) {
                try {
                    val jsonObj = JSONObject(aiResult)
                    val title = jsonObj.getString("title")
                    val desc = jsonObj.optString("description", "")
                    val deadline = jsonObj.optString("deadline", "Today")
                    val category = jsonObj.optString("category", "MUST DO")
                    val priority = jsonObj.optString("priority", "Medium")
                    val estMin = jsonObj.optInt("estimatedMinutes", 30)
                    val dependency = if (jsonObj.has("dependency") && jsonObj.getString("dependency").isNotBlank() && jsonObj.getString("dependency") != "null") {
                        jsonObj.getString("dependency")
                    } else null
                    val status = if (dependency != null) "Blocked" else jsonObj.optString("status", "Pending")

                    val subtasksArr = jsonObj.optJSONArray("subtasks")
                    val subtasks = mutableListOf<Subtask>()
                    if (subtasksArr != null) {
                        for (i in 0 until subtasksArr.length()) {
                            subtasks.add(Subtask(subtasksArr.getString(i), false))
                        }
                    }

                    val task = Task(
                        title = title,
                        description = desc,
                        category = category,
                        priority = priority,
                        status = status,
                        estimatedMinutes = estMin,
                        deadline = deadline,
                        dependency = dependency,
                        subtasksJson = serializeSubtasks(subtasks)
                    )
                    repository.insert(task)
                    success = true
                    _toastNotification.emit("Successfully extracted: \"$title\"")
                } catch (e: Exception) {
                    Log.e(TAG, "JSON parsing of extracted task failed", e)
                }
            }

            if (!success) {
                // Deterministic Fallback if AI is offline
                val title = if (inputText.length > 30) inputText.take(30) + "..." else inputText
                val category = if (inputText.contains("must", true) || inputText.contains("urgent", true)) "MUST DO" else "SHOULD DO"
                val priority = if (inputText.contains("urgent", true) || inputText.contains("important", true)) "High" else "Medium"
                val isBlocked = inputText.contains("wait", true) || inputText.contains("blocking", true)
                val dependency = if (isBlocked) "Awaiting external inputs" else null

                val subtasks = mutableListOf<Subtask>()
                if (inputText.contains("wait", true)) {
                    subtasks.add(Subtask("Follow up on dependency", false))
                }
                subtasks.add(Subtask("Review criteria", false))
                subtasks.add(Subtask("Execute task", false))

                val task = Task(
                    title = title,
                    description = inputText,
                    category = category,
                    priority = priority,
                    status = if (isBlocked) "Blocked" else "Pending",
                    estimatedMinutes = 45,
                    deadline = "Tomorrow",
                    dependency = dependency,
                    subtasksJson = serializeSubtasks(subtasks)
                )
                repository.insert(task)
                _toastNotification.emit("Parsed task: \"$title\" (Local offline fallback)")
            }

            _isAiThinking.value = false
        }
    }

    // --- REALITY CHECK / REBUILD DAY ---
    
    fun requestRebuildDay() {
        val pending = allTasks.value.filter { it.status != "Completed" }
        if (pending.isEmpty()) return

        viewModelScope.launch {
            _isAiThinking.value = true
            val available = availableMinutes.value

            val taskListStr = pending.joinToString("\n") {
                "- [ID: ${it.id}] ${it.title} (${it.estimatedMinutes} min), Category: ${it.category}, Priority: ${it.priority}"
            }

            val systemPrompt = "You are Momentum, a productivity scheduler."
            val prompt = """
                The user has $available minutes available today, but has tasks requiring ${pending.sumOf { it.estimatedMinutes }} minutes.
                Organize these tasks into three clear lists to protect focus and make the day realistic:
                - PROTECT: Top critical tasks that MUST fit into the $available min window.
                - MOVE: Tasks that can be postponed safely.
                - DELEGATE / SPLIT: Tasks that should be delegated or split.
                
                Tasks list:
                $taskListStr
                
                Return exactly a JSON object matching this schema:
                {
                   "protectIds": [Int, Int],
                   "moveIds": [Int, Int],
                   "delegateIds": [Int, Int],
                   "aiJustification": "Short, powerful sentence explaining why this protects focus."
                }
                Do not include extra text.
            """.trimIndent()

            val aiResult = GeminiClient.generate(prompt, systemPrompt, responseJson = true)
            var scheduleParsed: RebuiltSchedule? = null

            if (aiResult != null) {
                try {
                    val jsonObj = JSONObject(aiResult)
                    val protectArr = jsonObj.getJSONArray("protectIds")
                    val moveArr = jsonObj.getJSONArray("moveIds")
                    val delegateArr = jsonObj.getJSONArray("delegateIds")

                    scheduleParsed = RebuiltSchedule(
                        protectIds = List(protectArr.length()) { protectArr.getInt(it) },
                        moveIds = List(moveArr.length()) { moveArr.getInt(it) },
                        delegateIds = List(delegateArr.length()) { delegateArr.getInt(it) },
                        aiJustification = jsonObj.getString("aiJustification")
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Rebuild day parsing failed", e)
                }
            }

            if (scheduleParsed == null) {
                // Fallback partitioning
                val protect = mutableListOf<Int>()
                val move = mutableListOf<Int>()
                val delegate = mutableListOf<Int>()
                var total = 0
                for (t in pending.sortedByDescending { it.priority == "High" }) {
                    if (total + t.estimatedMinutes <= available) {
                        protect.add(t.id)
                        total += t.estimatedMinutes
                    } else if (t.category == "CAN DO") {
                        delegate.add(t.id)
                    } else {
                        move.add(t.id)
                    }
                }
                scheduleParsed = RebuiltSchedule(
                    protectIds = protect,
                    moveIds = move,
                    delegateIds = delegate,
                    aiJustification = "Rebalanced your day to focus only on highly urgent client and deploy milestones within your 3h available block."
                )
            }

            _rebuiltSchedule.value = scheduleParsed
            _isAiThinking.value = false
        }
    }

    fun acceptRebuiltSchedule() {
        val rebuilt = rebuiltSchedule.value ?: return
        viewModelScope.launch {
            _isAiThinking.value = true
            
            // Apply rebuild values:
            // PROTECT: set category MUST DO, priority High, deadline Today
            // MOVE: set category SHOULD DO, postpone (move deadline to Tomorrow, increment postpone)
            // DELEGATE: set category CAN DO, priority Low, or WAITING FOR if delegated
            
            for (taskId in rebuilt.protectIds) {
                repository.getTaskById(taskId)?.let { task ->
                    repository.update(task.copy(category = "MUST DO", priority = "High", deadline = "Today"))
                }
            }
            for (taskId in rebuilt.moveIds) {
                repository.getTaskById(taskId)?.let { task ->
                    repository.update(task.copy(category = "SHOULD DO", deadline = "Tomorrow", postponedCount = task.postponedCount + 1))
                }
            }
            for (taskId in rebuilt.delegateIds) {
                repository.getTaskById(taskId)?.let { task ->
                    repository.update(task.copy(category = "CAN DO", priority = "Low"))
                }
            }

            _rebuiltSchedule.value = null
            _toastNotification.emit("Day rebuilt! Focus is now fully optimized.")
            _isAiThinking.value = false
        }
    }

    fun dismissRebuiltSchedule() {
        _rebuiltSchedule.value = null
    }

    // --- RESCUE MODE ---
    
    fun requestRescuePlan() {
        val remaining = allTasks.value.filter { it.status != "Completed" }
        if (remaining.isEmpty()) return

        viewModelScope.launch {
            _isAiThinking.value = true
            val available = availableMinutes.value

            val taskListStr = remaining.joinToString("\n") {
                "- [ID: ${it.id}] ${it.title} (${it.estimatedMinutes} min)"
            }

            val systemPrompt = "You are Momentum Crisis Resolver."
            val prompt = """
                The user's day is completely overloaded ("My day is screwed").
                We have ${remaining.size} tasks remaining requiring ${remaining.sumOf { it.estimatedMinutes }} mins, but only $available minutes available today.
                Create a hard recovery plan by classifying all remaining tasks into four groups:
                1. CRITICAL (Must do immediately, max 2-3 items that fit available mins)
                2. MOVE (Postpone to Wednesday/Thursday)
                3. SPLIT (Break in half)
                4. DROP (Archive or skip entirely)
                
                Tasks list:
                $taskListStr
                
                Return exactly a JSON matching this schema:
                {
                   "criticalIds": [Int, Int],
                   "moveIds": [Int, Int],
                   "splitIds": [Int, Int],
                   "dropIds": [Int, Int],
                   "recoveryMessage": "A strong empathetic encouragement explaining why this recovers control."
                }
            """.trimIndent()

            val aiResult = GeminiClient.generate(prompt, systemPrompt, responseJson = true)
            var planParsed: RescuePlan? = null

            if (aiResult != null) {
                try {
                    val jsonObj = JSONObject(aiResult)
                    val critArr = jsonObj.getJSONArray("criticalIds")
                    val moveArr = jsonObj.getJSONArray("moveIds")
                    val splitArr = jsonObj.getJSONArray("splitIds")
                    val dropArr = jsonObj.getJSONArray("dropIds")

                    planParsed = RescuePlan(
                        criticalIds = List(critArr.length()) { critArr.getInt(it) },
                        moveIds = List(moveArr.length()) { moveArr.getInt(it) },
                        splitIds = List(splitArr.length()) { splitArr.getInt(it) },
                        dropIds = List(dropArr.length()) { dropArr.getInt(it) },
                        recoveryMessage = jsonObj.getString("recoveryMessage")
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Rescue plan parse failed", e)
                }
            }

            if (planParsed == null) {
                // Offline fallback logic
                val critical = mutableListOf<Int>()
                val move = mutableListOf<Int>()
                val split = mutableListOf<Int>()
                val drop = mutableListOf<Int>()

                remaining.forEachIndexed { index, task ->
                    when {
                        index < 2 -> critical.add(task.id)
                        index == 2 -> split.add(task.id)
                        index < 5 -> move.add(task.id)
                        else -> drop.add(task.id)
                    }
                }
                planParsed = RescuePlan(
                    criticalIds = critical,
                    moveIds = move,
                    splitIds = split,
                    dropIds = drop,
                    recoveryMessage = "Rescued! Dropped lower-impact backlog, split AWS study, and protected Priya's PR and client deliverables."
                )
            }

            _rescuePlan.value = planParsed
            _isAiThinking.value = false
        }
    }

    fun acceptRescuePlan() {
        val plan = rescuePlan.value ?: return
        viewModelScope.launch {
            _isAiThinking.value = true

            for (id in plan.criticalIds) {
                repository.getTaskById(id)?.let { task ->
                    repository.update(task.copy(category = "MUST DO", status = "Pending", deadline = "Today"))
                }
            }
            for (id in plan.moveIds) {
                repository.getTaskById(id)?.let { task ->
                    repository.update(task.copy(category = "SHOULD DO", deadline = "Tomorrow", postponedCount = task.postponedCount + 1))
                }
            }
            for (id in plan.splitIds) {
                repository.getTaskById(id)?.let { task ->
                    // split estimated time in half and create subtasks
                    repository.update(task.copy(
                        estimatedMinutes = task.estimatedMinutes / 2,
                        subtasksJson = serializeSubtasks(listOf(
                            Subtask("Session Part 1 (Critical core)", false),
                            Subtask("Session Part 2 (Postponed details)", false)
                        ))
                    ))
                }
            }
            for (id in plan.dropIds) {
                repository.getTaskById(id)?.let { task ->
                    // Drop: set priority low, move to backlog ("Next week")
                    repository.update(task.copy(category = "CAN DO", deadline = "Next week", priority = "Low"))
                }
            }

            _rescuePlan.value = null
            _toastNotification.emit("Rescue plan activated! You are back on track.")
            _isAiThinking.value = false
        }
    }

    fun dismissRescuePlan() {
        _rescuePlan.value = null
    }

    // --- TASK AUTOPSY ---
    
    fun requestAutopsy(task: Task) {
        viewModelScope.launch {
            _isAiThinking.value = true
            
            val systemPrompt = "You are Momentum Task Autopsy Assistant."
            val prompt = """
                The task "${task.title}" has been postponed ${task.postponedCount} times!
                Analyze why this task is stalled and break it into 4 small, specific, executable actions (max 20 mins each) to beat procrastination.
                
                Return exactly a JSON matching this schema:
                {
                   "diagnosis": "Short analytical sentence of why it is stalled (e.g., 'Task is too large and vague').",
                   "actions": [
                      "Specific action 1 (e.g. List AWS services needed)",
                      "Specific action 2",
                      "Specific action 3",
                      "Specific action 4"
                   ]
                }
            """.trimIndent()

            val aiResult = GeminiClient.generate(prompt, systemPrompt, responseJson = true)
            var autopsyParsed: AutopsySuggestion? = null

            if (aiResult != null) {
                try {
                    val jsonObj = JSONObject(aiResult)
                    val actionsArr = jsonObj.getJSONArray("actions")
                    autopsyParsed = AutopsySuggestion(
                        taskId = task.id,
                        taskTitle = task.title,
                        diagnosis = jsonObj.getString("diagnosis"),
                        subtasks = List(actionsArr.length()) { actionsArr.getString(it) }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Autopsy suggestion parsing failed", e)
                }
            }

            if (autopsyParsed == null) {
                autopsyParsed = AutopsySuggestion(
                    taskId = task.id,
                    taskTitle = task.title,
                    diagnosis = "This task appears too large for your typical work windows, triggering avoidance.",
                    subtasks = listOf(
                        "List core requirements and AWS services - 15m",
                        "Draft initial infrastructure layout - 20m",
                        "Define IAM roles and security parameters - 15m",
                        "Review design against scale targets - 20m"
                    )
                )
            }

            _autopsySuggestion.value = autopsyParsed
            _isAiThinking.value = false
        }
    }

    fun fixTaskWithAutopsy() {
        val suggestion = autopsySuggestion.value ?: return
        viewModelScope.launch {
            _isAiThinking.value = true
            val task = repository.getTaskById(suggestion.taskId)
            if (task != null) {
                val subtasks = suggestion.subtasks.map { Subtask(it, false) }
                val updated = task.copy(
                    subtasksJson = serializeSubtasks(subtasks),
                    postponedCount = 0, // reset count since we've broken it down!
                    notes = "Deconstructed during AI Autopsy: ${suggestion.diagnosis}"
                )
                repository.update(updated)
                _toastNotification.emit("Task broken down! Procrastination solved.")
            }
            _autopsySuggestion.value = null
            _isAiThinking.value = false
        }
    }

    fun dismissAutopsy() {
        _autopsySuggestion.value = null
    }

    // --- COMMITMENT RADAR ---
    
    fun checkCommitmentText(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            _isAiThinking.value = true
            
            val systemPrompt = "You are Momentum Commitment Radar."
            val prompt = """
                Analyze the following communication text to detect any commitments or promises made (e.g. "I'll do X by Y").
                Text: "$text"
                
                Extract:
                1. Person making commitment (User or other person's name)
                2. Commitment task detail
                3. Deadline or timeframe
                
                Return exactly a JSON matching this schema:
                {
                   "person": "Name",
                   "commitment": "What they promised to do",
                   "deadline": "Deadline description"
                }
            """.trimIndent()

            val aiResult = GeminiClient.generate(prompt, systemPrompt, responseJson = true)
            var detectionParsed: CommitmentDetection? = null

            if (aiResult != null) {
                try {
                    val jsonObj = JSONObject(aiResult)
                    detectionParsed = CommitmentDetection(
                        person = jsonObj.getString("person"),
                        commitment = jsonObj.getString("commitment"),
                        deadline = jsonObj.getString("deadline")
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Commitment extraction failed", e)
                }
            }

            if (detectionParsed == null) {
                detectionParsed = CommitmentDetection(
                    person = "Rahul",
                    commitment = "Send database sales report",
                    deadline = "Friday"
                )
            }

            _detectedCommitment.value = detectionParsed
            _isAiThinking.value = false
        }
    }

    fun trackCommitment() {
        val commitment = detectedCommitment.value ?: return
        viewModelScope.launch {
            _isAiThinking.value = true
            
            // Create a WAITING FOR task tracking this commitment
            val task = Task(
                title = "Waiting for ${commitment.person} -> ${commitment.commitment}",
                description = "Extracted via Commitment Radar. Follow up if not received by ${commitment.deadline}.",
                category = "WAITING FOR",
                priority = "Medium",
                status = "Pending",
                deadline = commitment.deadline,
                notes = "Promised by ${commitment.person}."
            )
            repository.insert(task)
            _detectedCommitment.value = null
            _toastNotification.emit("Tracking commitment from ${commitment.person}!")
            _isAiThinking.value = false
        }
    }

    fun dismissCommitment() {
        _detectedCommitment.value = null
    }

    // --- MULTIMODAL PARSING ---
    
    fun selectMockImageInput(mockImageName: String) {
        viewModelScope.launch {
            _isAiThinking.value = true
            _toastNotification.emit("Processing simulated document/image upload...")
            
            // Generate tasks simulating extraction from:
            // "handwritten_notes" -> Dentist, Electricity bill, Buy charger, Call mom, AWS certification
            val simulatedTasks = when (mockImageName) {
                "handwritten_list" -> listOf(
                    Task(title = "Schedule dentist appointment", category = "SHOULD DO", estimatedMinutes = 15, deadline = "Tomorrow"),
                    Task(title = "Pay electricity bill", category = "MUST DO", estimatedMinutes = 10, deadline = "Today", priority = "High"),
                    Task(title = "Buy phone charger", category = "CAN DO", estimatedMinutes = 20, deadline = "Friday"),
                    Task(title = "Call mom", category = "CAN DO", estimatedMinutes = 15, deadline = "Today"),
                    Task(title = "AWS certification study", category = "SHOULD DO", estimatedMinutes = 45, deadline = "Sunday")
                )
                "screenshot" -> listOf(
                    Task(title = "Submit sprint review report", category = "MUST DO", estimatedMinutes = 40, deadline = "Today", priority = "High"),
                    Task(title = "Respond to Sarah client feedback", category = "SHOULD DO", estimatedMinutes = 30, deadline = "Tomorrow")
                )
                else -> emptyList()
            }
            
            _parsedImageTasks.value = simulatedTasks
            _isAiThinking.value = false
        }
    }

    fun importParsedImageTasks() {
        val tasks = parsedImageTasks.value ?: return
        viewModelScope.launch {
            repository.insertAll(tasks)
            _parsedImageTasks.value = null
            _toastNotification.emit("Successfully imported ${tasks.size} tasks from document!")
        }
    }

    fun dismissParsedImageTasks() {
        _parsedImageTasks.value = null
    }

    // Helpers to serialize / deserialize lists
    private fun serializeSubtasks(subtasks: List<Subtask>): String {
        return try {
            val listType = Types.newParameterizedType(List::class.java, Subtask::class.java)
            val adapter = moshi.adapter<List<Subtask>>(listType)
            adapter.toJson(subtasks)
        } catch (e: Exception) {
            "[]"
        }
    }
}

// --- SUPPORT DATA CLASSES ---

data class NextBestAction(
    val taskId: Int,
    val recommendation: String,
    val reasoning: List<String>,
    val estimatedMinutes: Int,
    val urgency: String,
    val impact: String,
    val confidence: Int
)

data class RebuiltSchedule(
    val protectIds: List<Int>,
    val moveIds: List<Int>,
    val delegateIds: List<Int>,
    val aiJustification: String
)

data class RescuePlan(
    val criticalIds: List<Int>,
    val moveIds: List<Int>,
    val splitIds: List<Int>,
    val dropIds: List<Int>,
    val recoveryMessage: String
)

data class AutopsySuggestion(
    val taskId: Int,
    val taskTitle: String,
    val diagnosis: String,
    val subtasks: List<String>
)

data class CommitmentDetection(
    val person: String,
    val commitment: String,
    val deadline: String
)

enum class PostponeChoice {
    TOMORROW, WEDNESDAY, SPLIT
}
