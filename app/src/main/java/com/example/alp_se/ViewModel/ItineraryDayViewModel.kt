package com.example.alp_se.ViewModel

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.alp_se.Model.*
import com.example.alp_se.Repository.ItineraryDayRepository
import com.example.alp_se.TourronApplication
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import java.io.File
import java.io.FileOutputStream

class ItineraryDayViewModel(
    private val itineraryDayRepository: ItineraryDayRepository
) : ViewModel() {

    // State untuk form input
    var day by mutableStateOf("")
    var start_time by mutableStateOf("")
    var end_time by mutableStateOf("")
    var activity_description by mutableStateOf("")
    var meeting_time by mutableStateOf("")
    var itineraryId by mutableStateOf(0)
    var startDate by mutableStateOf<String?>(null)
    var endDate by mutableStateOf<String?>(null)


    // State untuk response data
    private val _itineraryDayModel = MutableStateFlow<List<ItineraryDayModel>>(emptyList())
    val itineraryDayModel: StateFlow<List<ItineraryDayModel>> = _itineraryDayModel

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun updateDay(value: String) { day = value }
    fun updateStartTime(value: String) { start_time = value }
    fun updateEndTime(value: String) { end_time = value }
    fun updateActivityDescription(value: String) { activity_description = value }
    fun updateMeetingTime(value: String) { meeting_time = value }
    fun updateItineraryId(value: Int) { itineraryId = value }
    fun updateStartDate(value: String) { startDate = value }
    fun updateEndDate(value: String) { endDate = value }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun getAllItineraryDays() {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.getAllItineraryDays()
                if (response.isSuccessful) {
                    _itineraryDayModel.value = response.body()?.data ?: emptyList()
                    _statusMessage.value = "Data loaded successfully."
                } else {
                    _statusMessage.value = "Failed to load data: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun createItineraryDay(request: CreateItineraryDayRequest) {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.createItineraryDay(request)
                if (response.isSuccessful) {
                    _statusMessage.value = "Created successfully."
                    getAllItineraryDays()
                } else {
                    _statusMessage.value = "Failed to create: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateItineraryDay(id: Int, request: UpdateItineraryDayRequest) {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.updateItineraryDay(id, request)
                if (response.isSuccessful) {
                    _statusMessage.value = "Updated successfully."
                    getAllItineraryDays()
                } else {
                    _statusMessage.value = "Failed to update: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteItineraryDay(id: Int) {
        viewModelScope.launch {
            try {
                val response = itineraryDayRepository.deleteItineraryDay(id)
                if (response.isSuccessful) {
                    _statusMessage.value = "Deleted successfully."
                    getAllItineraryDays()
                } else {
                    _statusMessage.value = "Failed to delete: ${response.message()}"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun exportToPdf(itineraryDays: List<ItineraryDayModel>, context: android.content.Context) {
        viewModelScope.launch {
            try {
                val document = Document()
                val pdfDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val pdfFile = File(pdfDir, "itinerary_export.pdf")
                PdfWriter.getInstance(document, FileOutputStream(pdfFile))

                document.open()
                document.add(Paragraph("Itinerary Days\n\n"))

                itineraryDays.forEachIndexed { index, item ->
                    document.add(
                        Paragraph(
                            "Day ${index + 1}:\n" +
                                    "Date: ${item.day}\n" +
                                    "Start: ${item.start_time}, End: ${item.end_time}\n" +
                                    "Activity: ${item.activity_description}\n\n"
                        )
                    )
                }

                document.close()
                _statusMessage.value = "Exported to PDF: ${pdfFile.absolutePath}"
            } catch (e: Exception) {
                _statusMessage.value = "PDF export failed: ${e.message}"
            }
        }
    }

    fun exportToExcel(itineraryDays: List<ItineraryDayModel>, context: Context) {
        viewModelScope.launch {
            try {
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Itinerary")

                // Header Row
                val headerRow = sheet.createRow(0)
                val headers = listOf("Day", "Start Time", "End Time", "Description", "Meeting Time")

                headers.forEachIndexed { index, title ->
                    val cell = headerRow.createCell(index)
                    cell.setCellValue(title)
                }

                // Data Rows
                itineraryDays.forEachIndexed { index, item ->
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(item.day)
                    row.createCell(1).setCellValue(item.start_time)
                    row.createCell(2).setCellValue(item.end_time)
                    row.createCell(3).setCellValue(item.activity_description ?: "")
                    row.createCell(4).setCellValue(item.meeting_time ?: "")
                }

                // Autosize
                for (i in headers.indices) {
                    sheet.autoSizeColumn(i)
                }

                // Save File
                val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                val file = File(fileDir, "itinerary_export.xlsx")
                val outputStream = FileOutputStream(file)
                workbook.write(outputStream)
                outputStream.close()
                workbook.close()

                _statusMessage.value = "Exported to Excel: ${file.absolutePath}"
            } catch (e: Exception) {
                _statusMessage.value = "Excel export failed: ${e.message}"
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TourronApplication)
                val repository = application.container.itineraryDayRepository
                ItineraryDayViewModel(repository)
            }
        }
    }

}