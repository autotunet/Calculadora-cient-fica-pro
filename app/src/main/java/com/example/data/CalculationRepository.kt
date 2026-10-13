package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val dao: CalculationDao) {
    val historyFlow: Flow<List<CalculationItem>> = dao.getAllHistory()

    suspend fun saveCalculation(expression: String, result: String, angleUnit: String) {
        dao.insertCalculation(
            CalculationItem(
                expression = expression,
                result = result,
                angleUnit = angleUnit
            )
        )
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }

    suspend fun deleteItem(id: Long) {
        dao.deleteById(id)
    }
}
