package com.example.bckgrnd

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bckgrnd.Model.Photo
import com.example.bckgrnd.Model.Tag
import com.example.bckgrnd.Model.tblLocation
import com.example.bckgrnd.Remote.IApi
import com.example.bckgrnd.Remote.RetroFitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class UploadActivity : AppCompatActivity() {
    var pickedPhoto: Uri? = null
    var pickedPhotoBitMap: Bitmap? = null
    var pickedPhotoBase64: String? = null
    lateinit var iApi: IApi
    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        iApi = RetroFitClient.getInstance().create(IApi::class.java)

        val btnSelectPhoto = findViewById<ImageView>(R.id.ivSelectImage)
        btnSelectPhoto.setOnClickListener {
            pickPhoto()
        }

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            val name = findViewById<EditText>(R.id.etLocationName).text.toString()
            val coordinates = findViewById<EditText>(R.id.etCoordinates).text.split(" ")
            val photo = Photo(pickedPhotoBase64)

            if(name.isNotEmpty() && areCoordinatesValid(coordinates)) {
                try {
                    val location = tblLocation(
                        name,
                        "AAAA",
                        coordinates[0].toFloat(),
                        coordinates[1].toFloat(),
                        listOf(Tag("History")),
                        listOf(photo))

                    compositeDisposable.addAll(iApi.addLocation(location)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {s ->
                                this@UploadActivity.finish()
                                Toast.makeText(this@UploadActivity, s, Toast.LENGTH_SHORT).show()
                            },
                            {t: Throwable ->
                                Log.i("MESSAGE", t.message.toString())
                                Toast.makeText(this@UploadActivity, t.message, Toast.LENGTH_LONG).show()
                            })
                    )
                } catch(_: Exception) {

                }
            } else {
                Toast.makeText(this@UploadActivity, "Invalid coordinates or empty name field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areCoordinatesValid(coords: List<String>): Boolean {
        try {
            if (coords.size != 2
                || (coords[0].toFloat() < 54.577634 || coords[0].toFloat() > 54.803851)
                || (coords[1].toFloat() < 25.107056 || coords[1].toFloat() > 25.518219)) {
                return false
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun pickPhoto() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
        }
    }

    private fun photoToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b: ByteArray = baos.toByteArray()

        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null ) {
            pickedPhoto = data.data
            pickedPhotoBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver, pickedPhoto)
            val btnSelectPhoto = findViewById<ImageView>(R.id.ivSelectImage)
            btnSelectPhoto.setImageBitmap(pickedPhotoBitMap)
            pickedPhotoBase64 = photoToBase64(pickedPhotoBitMap!!)
            Log.i("MESSAGE", pickedPhotoBase64!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}