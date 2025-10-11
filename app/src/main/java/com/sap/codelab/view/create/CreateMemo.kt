package com.sap.codelab.view.create

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sap.codelab.R
import com.sap.codelab.databinding.ActivityCreateMemoBinding
import com.sap.codelab.utils.extensions.empty
import com.sap.codelab.view.map.MapSelectActivity

internal class CreateMemo : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMemoBinding
    private lateinit var model: CreateMemoViewModel

    private val pickLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val lat = result.data?.getDoubleExtra(MapSelectActivity.EXTRA_LAT, Double.NaN) ?: Double.NaN
                val lng = result.data?.getDoubleExtra(MapSelectActivity.EXTRA_LNG, Double.NaN) ?: Double.NaN
                if (!lat.isNaN() && !lng.isNaN()) {
                    model.updateLocation(lat, lng)
                    binding.contentCreateMemo.tvLocationState.text = getString(R.string.location_set)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        model = ViewModelProvider(this)[CreateMemoViewModel::class.java]

        // Open map picker
        binding.contentCreateMemo.btnPickOnMap.setOnClickListener {
            model.updateMemo(
                binding.contentCreateMemo.memoTitle.text.toString(),
                binding.contentCreateMemo.memoDescription.text.toString()
            )
            pickLocation.launch(Intent(this, MapSelectActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_memo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> { saveMemo(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveMemo() {
        binding.contentCreateMemo.run {
            model.updateMemo(memoTitle.text.toString(), memoDescription.text.toString())
            val isValid = model.isMemoValid()
            val hasLocation = model.hasLocation()

            if (isValid && hasLocation) {
                model.saveMemo()
                setResult(RESULT_OK)
                finish()
            } else {
                memoTitleContainer.error = getErrorMessage(model.hasTitleError(), R.string.memo_title_empty_error)
                memoDescription.error = getErrorMessage(model.hasTextError(), R.string.memo_text_empty_error)
                if (!hasLocation) {
                    tvLocationState.text = getString(R.string.location_required)
                }
            }
        }
    }

    private fun getErrorMessage(hasError: Boolean, @StringRes errorMessageResId: Int): String {
        return if (hasError) getString(errorMessageResId) else String.empty()
    }
}