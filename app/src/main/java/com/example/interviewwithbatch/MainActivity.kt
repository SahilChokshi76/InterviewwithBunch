package com.example.interviewwithbatch

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.interviewwithbatch.adapter.ListAdapter
import com.example.interviewwithbatch.databinding.ActivityMainBinding
import com.example.interviewwithbatch.model.User
import com.example.interviewwithbatch.viewModel.UserViewModel
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        insertEmpData()

        showEmpData()

        binding.generateBT.setOnClickListener { genrateExcel() }
    }

    private fun genrateExcel() {

        val workbook = HSSFWorkbook()
        val firstSheet = workbook.createSheet("Employee Details")

        val headerRow = firstSheet.createRow(0)
        var cell = headerRow.createCell(0)

        cell.setCellValue(HSSFRichTextString("Emp Id"))

        cell = headerRow.createCell(1)
        cell.setCellValue(HSSFRichTextString("Emp Name"))

        val listdata: MutableList<User> = ArrayList()

        mUserViewModel.readAllData.observe(this, Observer {
            listdata.addAll(it)
        })

        for (i in listdata.indices) {

            val bodyRow = firstSheet.createRow(i+1)

            cell = bodyRow.createCell(0)
            cell.setCellValue(HSSFRichTextString(listdata[i].id.toString()))

            cell = bodyRow.createCell(1)
            cell.setCellValue(HSSFRichTextString(listdata[i].firstName))

        }


        val file = File(this.getExternalFilesDir(null), "EmployeeDetails.xls")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            workbook.write(fos)
            val fileUri: Uri? = initiateSharing()
            if (fileUri != null) {
                launchShareFileIntent(fileUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }

    }

    fun initiateSharing(): Uri? {
        return accessFile(this, "EmployeeDetails.xls")
    }

    fun accessFile(context: Context, fileName: String?): Uri? {
        val file = File(context.getExternalFilesDir(null), fileName)
        return if (file.exists()) {
            FileProvider.getUriForFile(
                context,
                "com.example.interviewwithbatch", file
            )

        } else {
            null
        }
    }

    private fun launchShareFileIntent(uri: Uri) {
       /* val intent = ShareCompat.IntentBuilder.from(this)
            .setType("application/pdf")
            .setStream(uri)
            .setChooserTitle("Select application to share file")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)*/

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("sahil.chokshi13@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Employees List")
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Sahil, I attached an excel file of our employees")
        try {
            this.startActivity(Intent.createChooser(emailIntent, "Send to..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No Intent matcher found", Toast.LENGTH_LONG).show()
        }

    }


    private fun insertEmpData() {

     // <- Pass id, firstName. Although id will be auto-generated because it is a primary key, we need to pass a value or zero (Don't worry, the Room library knows it is the primary key and is auto-generated).

        val userData: List<User> = arrayListOf( User(1, "Sahil"),
            User(2, "Sanket"),
            User(3, "Rahul"),
            User(4, "Parth"),
            User(5, "Mike"),
            User(6, "Mark"),
            User(7, "Michael"),
            User(8, "Lieven"),
            User(9, "Robert"),
            User(10, "Raffael"),
            User(11, "Daniel"),
            User(12, "Andreas"),
            User(13, "Martin"),
            User(14, "Betsy"),
            User(15, "Debbie"),
            User(16, "Alexei"),
            User(17, "David"),
            User(18, "Frank"),
            User(19, "Samuel"),
            User(20, "Bruno"))

        // Add Data to database
        mUserViewModel.addUser(userData)
    }

    private fun showEmpData() {
        val adapter = ListAdapter()

        val recyclerView = binding.listRV

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mUserViewModel.readAllData.observe(this, Observer {
            adapter.setData(it)
        })
    }
}