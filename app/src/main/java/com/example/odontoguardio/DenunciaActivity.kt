package com.example.odontoguardio

import DatabaseManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.mailjet.client.resource.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout


class DenunciaActivity : AppCompatActivity() {

    val options = listOf(
        // NORTE group
        SpinnerItem("ALTO BRANCO ARAXÁ - NORTE", 1),
        SpinnerItem("BELA VISTA - NORTE", 1),
        SpinnerItem("CENTRO CONCEIÇÃO - NORTE", 1),
        SpinnerItem("CUÍTES - NORTE", 1),
        SpinnerItem("JARDIM CONTINENTAL - NORTE", 1),
        SpinnerItem("JARDIM TAVARES - NORTE", 1),
        SpinnerItem("JEREMIAS - NORTE", 1),
        SpinnerItem("LAURITZEN - NORTE", 1),
        SpinnerItem("LOUZEIRO - NORTE", 1),
        SpinnerItem("MONTE SANTO NAÇÕES - NORTE", 1),
        SpinnerItem("PALMEIRA - NORTE", 1),
        SpinnerItem("PRATA - NORTE", 1),
        SpinnerItem("ROSA MÍSTICA - NORTE", 1),
        SpinnerItem("SÃO JOSÉ - NORTE", 1),

        // SUL group
        SpinnerItem("ACACIO FIGUEIREDO - SUL", 2),
        SpinnerItem("ROCHA CAVALCANTI - SUL", 2),
        SpinnerItem("BAIRRO DAS CIDADES - SUL", 2),
        SpinnerItem("CATOLE DE BOA VISTA - SUL", 2),
        SpinnerItem("CINZA - SUL", 2),
        SpinnerItem("CRUZEIRO - SUL", 2),
        SpinnerItem("DISTRITO DOS MECÂNICOS - SUL", 2),
        SpinnerItem("DISTRITO INDUSTRIAL - SUL", 2),
        SpinnerItem("ESTAÇÃO VELHA - SUL", 2),
        SpinnerItem("JARDIM BORBOREMA - SUL", 2),
        SpinnerItem("JARDIM PAULISTANO - SUL", 2),
        SpinnerItem("LIBERDADE - SUL", 2),
        SpinnerItem("LIGEIRO - SUL", 2),
        SpinnerItem("NOVO HORIZONTE PRESIDENTE MÉDICE - SUL", 2),
        SpinnerItem("ROSA CRUZ - SUL", 2),
        SpinnerItem("RESSUREIÇÃO - SUL", 2),
        SpinnerItem("SÍTIO LUCAS - SUL", 2),
        SpinnerItem("SITÍO PAUS BRANCOS SÍTIO SALGADINHOS - SUL", 2),
        SpinnerItem("TAMBOR TRÊS IRMÃS - SUL", 2),
        SpinnerItem("VELAME - SUL", 2),
        SpinnerItem("CATINGUEIRA - SUL", 2),
        SpinnerItem("SÍTIO ESTREITO - SUL", 2),
        SpinnerItem("PORTAL SUDOESTE - SUL", 2),
        SpinnerItem("MAJOR VENEZIANO - SUL", 2),
        SpinnerItem("JARDIM VERDEJANTE - SUL", 2),
        SpinnerItem("CATOLE DO ZÉ FERREIRA - SUL", 2),

        // LESTE group
        SpinnerItem("ANTIGA CACHOEIRA - LESTE", 3),
        SpinnerItem("SANDRA CAVALCANTE - LESTE", 3),
        SpinnerItem("BELO MONTE - LESTE", 3),
        SpinnerItem("CASTELO BRANCO - LESTE", 3),
        SpinnerItem("CATOLE - LESTE", 3),
        SpinnerItem("GALANTE - LESTE", 3),
        SpinnerItem("GLÓRIA 1 e 2 - LESTE", 3),
        SpinnerItem("ITARARÉ - LESTE", 3),
        SpinnerItem("JARDIM AMÉRICA - LESTE", 3),
        SpinnerItem("JARDIM ATALAIA - LESTE", 3),
        SpinnerItem("JARDIM EUROPA - LESTE", 3),
        SpinnerItem("JOSÉ PINHEIRO - LESTE", 3),
        SpinnerItem("MIRANTE - LESTE", 3),
        SpinnerItem("MONTE CASTELO - LESTE", 3),
        SpinnerItem("NOVA BRASILIA - LESTE", 3),
        SpinnerItem("SANTA TEREZINHA - LESTE", 3),
        SpinnerItem("PORTEIRA DE PEDRA - LESTE", 3),
        SpinnerItem("SANTO ANTONIO - LESTE", 3),
        SpinnerItem("SÍTIO PAU DENTRO - LESTE", 3),
        SpinnerItem("SÍTIO BRITO - LESTE", 3),
        SpinnerItem("SÍTIO CARIDADE - LESTE", 3),
        SpinnerItem("SÍTIO CHÃ DE DENTRO - LESTE", 3),
        SpinnerItem("SÍTIO DE BAIXO/SÍTIO DE CIMA - LESTE", 3),
        SpinnerItem("SÍTIO LARANJEIRA - LESTE", 3),
        SpinnerItem("SÍTIO MARINHO - LESTE", 3),
        SpinnerItem("SÍTIO MASSAPÉ - LESTE", 3),
        SpinnerItem("SÍTIO NOVA VARZÉA - LESTE", 3),
        SpinnerItem("SÍTIO RAMO - LESTE", 3),
        SpinnerItem("SÍTIO SANTANA - LESTE", 3),
        SpinnerItem("SÍTIO SÃO JORGE - LESTE", 3),
        SpinnerItem("SÍTIO SIRUDO - LESTE", 3),
        SpinnerItem("VILA CABRAL SANTA TEREZINHA - LESTE", 3),
        SpinnerItem("VILA PARQUE MARIA DA LUZ - LESTE", 3),
        SpinnerItem("COMPLEXO ALUÍZIO CAMPOS - LESTE", 3),

        // OESTE group
        SpinnerItem("BODOCONGÓ - OESTE", 4),
        SpinnerItem("PEDEGRAL - OESTE", 4),
        SpinnerItem("CAMPO DE ANGOLA I e II - OESTE", 4),
        SpinnerItem("CATIRINA - OESTE", 4),
        SpinnerItem("CENTENÁRIO - OESTE", 4),
        SpinnerItem("CONJ. DOS PROFESSORES - OESTE", 4),
        SpinnerItem("CONJUNTO MARIZ - OESTE", 4),
        SpinnerItem("CONJUNTO SONHO MEU DINAMERICA - OESTE", 4),
        SpinnerItem("GRANDE CAMPINA - OESTE", 4),
        SpinnerItem("LAGO DE DENTRO - OESTE", 4),
        SpinnerItem("MALVINAS - OESTE", 4),
        SpinnerItem("MORRO DO PINTO - OESTE", 4),
        SpinnerItem("MORRO DO URUBU - OESTE", 4),
        SpinnerItem("MUTIRÃO - OESTE", 4),
        SpinnerItem("QUARENTA - OESTE", 4),
        SpinnerItem("NOVO BODOCONGÓ - OESTE", 4),
        SpinnerItem("RAMADINHA - OESTE", 4),
        SpinnerItem("RIACHO DOS PORCOS - OESTE", 4),
        SpinnerItem("SANTA CRUZ - OESTE", 4),
        SpinnerItem("SANTA ROSA - OESTE", 4),
        SpinnerItem("SÃO JOSÉ DA MATA - OESTE", 4),
        SpinnerItem("SERRA I e II - OESTE", 4),
        SpinnerItem("SERROTÃO - OESTE", 4),
        SpinnerItem("SÍTIO BOSQUE - OESTE", 4),
        SpinnerItem("SITIO IZIDRO - OESTE", 4),
        SpinnerItem("SÍTIO JOAQUIM VIEIRA SÍTIO SÃO JANUÁRIO - OESTE", 4),
        SpinnerItem("UNIVERSITÁRIO - OESTE", 4),
        SpinnerItem("VILA CABRAL DE SANTA ROSA - OESTE", 4),
        SpinnerItem("JARDIM QUARENTA - OESTE", 4),
        SpinnerItem("SÍTIO CAPIM GRANDE - OESTE", 4),
        SpinnerItem("NOVO CAMPINA/CONJ. ALAMEDA - OESTE", 4),
        SpinnerItem("SEVERINO CABRAL - OESTE", 4)
    )
    private lateinit var btnBack: Button
    private lateinit var btnFormulario: Button
    private lateinit var rbAgressYes: RadioButton
    private lateinit var rbAgressNo: RadioButton
    private lateinit var lytSinaisAgressao: ConstraintLayout
    private lateinit var rgAgressionGroup: RadioGroup
    private lateinit var dbManager: DatabaseManager
    private lateinit var progressBar: ProgressBar
    private lateinit var etDataNascimento: EditText
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var textInputLayout: TextInputLayout
    private var email: String = ""

    private val radioGroupRelations = listOf(
        RadioGroupRelation(R.id.rgHematoma, R.id.rgHematomas2, R.id.rbHematomaS),
        RadioGroupRelation(R.id.rgAbrasoes, R.id.rgAbrasoes2, R.id.rbAbrasaoS),
        RadioGroupRelation(R.id.rgLaceracoes, R.id.rgLaceracoes2, R.id.rbLaceracaoS),
        RadioGroupRelation(R.id.rgQueimaduras, R.id.rgQueimaduras2, R.id.rbQueimaduraS),
        RadioGroupRelation(R.id.rgMordidas, R.id.rgMordidas2, R.id.rbMordidaS),
        RadioGroupRelation(R.id.rgFraturas, R.id.rgFraturas2, R.id.rbFraturaS)
        // Add more relationships as needed
    )

    private val radioGroupIds = listOf(
        R.id.rgSex,
        R.id.rgAgression
    )

    private val editTextIds = listOf(
        R.id.etNomeVitima,
        R.id.etDataNascimento,
        R.id.etMomento,
        R.id.etLocal,
        R.id.etMecanismo,
        R.id.etNegligencia,
        R.id.etResponsavel,
        R.id.etCrianca,
        R.id.etDetalhes
        // ... add all your EditText IDs here
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_denuncia)

        btnBack  = findViewById(R.id.btn_backd)
        btnFormulario  = findViewById(R.id.btnFormulario)
        rgAgressionGroup = findViewById(R.id.rgAgression)
        rbAgressYes = findViewById(R.id.rbAgrYes)
        rbAgressNo = findViewById(R.id.rbAgrNo)
        lytSinaisAgressao = findViewById(R.id.lyt_sinaisAgressao)
        progressBar= findViewById(R.id.progress_bar)
        dbManager = DatabaseManager()
        etDataNascimento = findViewById(R.id.etDataNascimento)
        textInputLayout = findViewById(R.id.spinner_layout)
        autoCompleteTextView = findViewById(R.id.auto_complete_textview)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        autoCompleteTextView.setAdapter(adapter)


        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = adapter.getItem(position)
            updateEmailBasedOnGroup(selectedItem)
            textInputLayout.error = null // Clear any previous error
        }

        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateInput()
            }
        }


        for (radioGroupId in radioGroupIds) {
            val radioGroup = findViewById<RadioGroup>(radioGroupId)
            radioGroup.setOnCheckedChangeListener { group, _ ->
                resetRadioGroupBackground(group)
            }
        }






        etDataNascimento.addTextChangedListener(DateInputWatcher(etDataNascimento))

        etDataNascimento.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateAndFormatDate(etDataNascimento)
            }
        }



        rgAgressionGroup.setOnCheckedChangeListener { group, checkedId ->
            resetRadioGroupBackground(group)
            when (checkedId) {
                R.id.rbAgrYes -> {
                    lytSinaisAgressao.visibility = View.VISIBLE
                }
                else -> {
                    lytSinaisAgressao.visibility = View.GONE
                    clearAdditionalFieldsSelections()
                }
            }
        }

        btnFormulario.setOnClickListener{
            if (validateInputs()) {
                if(validateInput()){
                    if (email != "") {
                        sendPdfByEmail(email)
                    } else {
                        Toast.makeText(this, "Por favor, selecione seu bairro", Toast.LENGTH_LONG)
                            .show()
                    }
                }else{
                    Toast.makeText(this, "Falha ao selecionar bairro", Toast.LENGTH_LONG).show()
                }



            }else{
                Toast.makeText(this, "Por favor, preencha todos os campos necessários", Toast.LENGTH_SHORT).show()
            }
        }


        btnBack.setOnClickListener {
            finish()
        }

        setupRadioGroupListeners()

    }






    private fun sendPdfByEmail(email: String) {

        progressBar.visibility = View.VISIBLE
        disableUserInteraction()

        lifecycleScope.launch {
            try {
                // Generate the PDF as ByteArray
                val formData = collectFormData()
                val pdfBytes = createPdfDocument(formData)

                // Send the email with attachment
                val emailSent = dbManager.sendEmailWithAttachment(pdfBytes, email)

                withContext(Dispatchers.Main) {
                    // Hide ProgressBar and enable interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()

                    if (emailSent) {
                        Toast.makeText(this@DenunciaActivity, "Denuncia enviada com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DenunciaActivity, "Falha ao enviar denúncia", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Hide ProgressBar and enable interaction
                    progressBar.visibility = View.GONE
                    enableUserInteraction()

                    Toast.makeText(this@DenunciaActivity, "Ocorreu um erro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun updateEmailBasedOnGroup(selectedItem: SpinnerItem?) {
        selectedItem?.let {
            email = when (it.group) {
                1 -> "gabsec4@gmail.com"
                2 -> "gabsec3@gmail.com"
                3 -> "backgab3@gmail.com"
                4 -> "gabriel.lima@ccc.ufcg.edu.br"
                else -> ""
            }
        }
    }



    private fun validateInput(): Boolean {
        val inputText = autoCompleteTextView.text.toString()
        val selectedItem = options.find { it.name.equals(inputText, ignoreCase = true) }
        return if (selectedItem != null) {
            updateEmailBasedOnGroup(selectedItem)
            textInputLayout.error = null
            true
        } else {
            textInputLayout.error = "No matching item found"
            false
        }
    }

    private fun validateInputs(): Boolean {
        var allValid = true

        // Validate RadioGroups
        for (radioGroupId in radioGroupIds) {
            val radioGroup = findViewById<RadioGroup>(radioGroupId)
            if (radioGroup.checkedRadioButtonId == -1) {
                allValid = false
                radioGroup.background = ContextCompat.getDrawable(this, R.drawable.error_background)
            } else {
                // Reset to default background if it's valid
                radioGroup.background = null // or set to your default background
            }
        }
        // Validate EditTexts
        for (editTextId in editTextIds) {
            val editText = findViewById<EditText>(editTextId)
            if (editText.text.toString().trim().isEmpty()) {
                allValid = false
                editText.error = "This field is required"
                // break  // If you want to stop at the first error
            }
        }

        return allValid
    }

    private fun resetRadioGroupBackground(radioGroup: RadioGroup) {
        radioGroup.background = null // or set to your default background
    }



    private fun setupRadioGroupListeners() {
        radioGroupRelations.forEach { relation ->
            val parentGroup = findViewById<RadioGroup>(relation.parentGroupId)
            val childGroup = findViewById<RadioGroup>(relation.childGroupId)

            parentGroup.setOnCheckedChangeListener { _, checkedId ->
                val enable = checkedId == relation.enableOnYesId
                enableRadioGroup(childGroup, enable)
                if (!enable) {
                    childGroup.clearCheck()
                }
            }
        }
    }

    private fun enableRadioGroup(radioGroup: RadioGroup, enable: Boolean) {
        for (i in 0 until radioGroup.childCount) {
            val child = radioGroup.getChildAt(i)
            if (child is RadioButton) {
                child.isEnabled = enable
                child.alpha = if (enable) 1.0f else 0.5f
            }
        }
    }



    private fun clearAdditionalFieldsSelections() {
        // Clear selections for all RadioGroups in the additional fields layout
        lytSinaisAgressao.findViewsWithType(RadioGroup::class.java).forEach { radioGroup ->
            radioGroup.clearCheck()
        }
    }

    fun <T : View> ViewGroup.findViewsWithType(type: Class<T>): List<T> {
        val result = mutableListOf<T>()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (type.isInstance(child)) {
                result.add(child as T)
            }
            if (child is ViewGroup) {
                result.addAll(child.findViewsWithType(type))
            }
        }
        return result
    }

    /*
    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an explanation to the user
            // You can show a dialog explaining why you need this permission
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePDF(collectFormData())
            } else {
                Toast.makeText(this, "Permission denied. Cannot generate PDF.", Toast.LENGTH_SHORT).show()
                // Permission denied, handle this case (e.g., show a message to the user)
            }
        }
    }


     */

    /*
    fun generatePDF(formData: FormData) {
        val displayName = "Formulario_Denuncia${System.currentTimeMillis()}.pdf"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/YourAppName")
            }
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                //createPdfDocument(outputStream, formData)
            }
        }
        Toast.makeText(this, "PDF generated successfully", Toast.LENGTH_SHORT).show()
    }

     */


    private fun validateAndFormatDate(editText: EditText) {
        val input = editText.text.toString()
        if (input.length == 10) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.isLenient = false
                val date = sdf.parse(input)
                if (date != null) {
                    // Valid date, you can format it if needed
                    editText.setText(sdf.format(date))
                } else {
                    // Invalid date
                    editText.error = "Invalid date"
                }
            } catch (e: ParseException) {
                // Invalid date
                editText.error = "Invalid date"
            }
        } else if (input.isNotEmpty()) {
            // Incomplete date
            editText.error = "Please enter a complete date"
        }
    }


    private fun createPdfDocument(formData: FormData): ByteArray{
        val outputStream = ByteArrayOutputStream()
        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)



        document.add(Paragraph("Data de nascimento: ${formData.dataNascimento}"))
        document.add(Paragraph("Sexo: ${formData.sexo}"))
        document.add(Paragraph("Sinais de agressão: ${formData.sinaisAgressao}"))
        if(formData.sinaisAgressao == "Sim"){
            document.add(Paragraph("Lesão no olho: ${formData.lesaoOlho}"))
            document.add(Paragraph("Hematomas: ${formData.hematoma}"))
            document.add(Paragraph("Abrasões: ${formData.abrasoes}"))
            document.add(Paragraph("Lacerações: ${formData.laceracoes}"))
            document.add(Paragraph("Queimaduras: ${formData.queimaduras}"))
            document.add(Paragraph("Mordidas: ${formData.mordidas}"))
            document.add(Paragraph("Fraturas: ${formData.fraturas}"))
            document.add(Paragraph("Laceração no freio labial: ${formData.hematoma}"))
            document.add(Paragraph("Laceração no freio lingual: ${formData.hematoma}"))
            document.add(Paragraph("Trauma no palato: ${formData.hematoma}"))
            document.add(Paragraph("Trauma dental: ${formData.hematoma}"))
        }
        document.add(Paragraph("Momento da injúria (Se relatou e quem relatou): ${formData.momentoInjuria}"))
        document.add(Paragraph("Local da injúria (se relatou e quem relatou): ${formData.localInjuria}"))
        document.add(Paragraph("Mecanismo da injúria (se relatou e quem relatou): ${formData.mecanismoInjuria}"))
        document.add(Paragraph("Alguma manifestação bucal suspeita de abuso sexual? qual?: ${formData.abusoSexual}"))
        document.add(Paragraph("Impressões quanto a negligência odontologica: ${formData.negligencia}"))
        document.add(Paragraph("Registro/impressão da história contada pelo responsável: ${formData.historiaResponsavel}"))
        document.add(Paragraph("Registro/impressão da história contada pela criança: ${formData.historiaCrianca}"))
        document.add(Paragraph("Detalhes e outras observações: ${formData.detalhes}"))

        document.close()
        return outputStream.toByteArray()
    }



    fun collectFormData(): FormData {
        return FormData(
            dataNascimento = findViewById<EditText>(R.id.etDataNascimento).text.toString(),
            sexo = when (findViewById<RadioGroup>(R.id.rgSex).checkedRadioButtonId) {
                R.id.rbMale -> "Masculino"
                R.id.rbFemale -> "Feminino"
                else -> "Não especificado"
            },
            sinaisAgressao = when (findViewById<RadioGroup>(R.id.rgAgression).checkedRadioButtonId) {
                R.id.rbAgrYes -> "Sim"
                R.id.rbAgrNo -> "Não"
                else -> "Não especificado"
            },
            laceracaolingual = when (findViewById<RadioGroup>(R.id.rgLingua).checkedRadioButtonId) {
                R.id.rbLingualS -> "Sim"
                R.id.rbLingualN -> "Não"
                else -> "Não especificado"
            },
            laceracaolabial = when (findViewById<RadioGroup>(R.id.rgLabial).checkedRadioButtonId) {
                R.id.rbLabialS -> "Sim"
                R.id.rbLabialN -> "Não"
                else -> "Não especificado"
            },
            lesaoOlho = when (findViewById<RadioGroup>(R.id.rgLesaoOlho).checkedRadioButtonId) {
                R.id.rbLesaoOlhoS -> "Sim"
                R.id.rbLesaoOlhoN -> "Não"
                else -> "Não especificado"
            },
            palato = when (findViewById<RadioGroup>(R.id.rgPalato).checkedRadioButtonId) {
                R.id.rbPalatoS -> "Sim"
                R.id.rbPalatoN -> "Não"
                else -> "Não especificado"
            },
            dental = when (findViewById<RadioGroup>(R.id.rgDental).checkedRadioButtonId) {
                R.id.rbDentalS -> "Sim"
                R.id.rbDentalN -> "Não"
                else -> "Não especificado"
            },
            hematoma = when (findViewById<RadioGroup>(R.id.rgHematomas2).checkedRadioButtonId) {
                R.id.rbHematomaCabeca -> "Hematoma na cabeça"
                R.id.rbHematomaFace -> "Hematoma na face"
                R.id.rbHematomaPesc -> "Hematoma no pescoço"
                else -> "Não/Não especificado"
            },
            abrasoes = when (findViewById<RadioGroup>(R.id.rgAbrasoes2).checkedRadioButtonId) {
                R.id.rbAbrasaoCabeca -> "Abrasão na cabeça"
                R.id.rbAbrasaoFace -> "Abrasão na face"
                R.id.rbAbrasaoPesc -> "Abrasão no pescoço"
                else -> "Não/Não especificado"
            },
            laceracoes = when (findViewById<RadioGroup>(R.id.rgLaceracoes2).checkedRadioButtonId) {
                R.id.rbLaceracaoCabeca -> "Laceração na cabeça"
                R.id.rbLaceracaoFace -> "Laceração na face"
                R.id.rbLaceracaoPesc -> "Laceração no pescoço"
                else -> "Não/Não especificado"
            },
            queimaduras = when (findViewById<RadioGroup>(R.id.rgQueimaduras2).checkedRadioButtonId) {
                R.id.rbQueimaduraCabeca -> "Queimadura na cabeça"
                R.id.rbQueimaduraFace -> "Queimadura na face"
                R.id.rbQueimaduraPesc -> "Queimadura no pescoço"
                else -> "Não/Não especificado"
            },
            mordidas = when (findViewById<RadioGroup>(R.id.rgMordidas2).checkedRadioButtonId) {
                R.id.rbMordidaCabeca -> "Mordida na cabeça"
                R.id.rbMordidaFace -> "Mordida na face"
                R.id.rbMordidaPesc -> "Mordida no pescoço"
                else -> "Não/Não especificado"
            },
            fraturas = when (findViewById<RadioGroup>(R.id.rgFraturas2).checkedRadioButtonId) {
                R.id.rbFraturaCabeca -> "Fratura na cabeça"
                R.id.rbFraturaFace -> "Fratura na face"
                R.id.rbFraturaPesc -> "Fratura no pescoço"
                else -> "Não/Não especificado"
            },
            momentoInjuria = findViewById<EditText>(R.id.etMomento).text.toString(),
            localInjuria = findViewById<EditText>(R.id.etLocal).text.toString(),
            mecanismoInjuria = findViewById<EditText>(R.id.etMecanismo).text.toString(),
            abusoSexual = findViewById<EditText>(R.id.etSexual).text.toString(),
            negligencia = findViewById<EditText>(R.id.etNegligencia).text.toString(),
            historiaResponsavel = findViewById<EditText>(R.id.etResponsavel).text.toString(),
            historiaCrianca = findViewById<EditText>(R.id.etCrianca).text.toString(),
            detalhes = findViewById<EditText>(R.id.etDetalhes).text.toString()

            // Collect data from other fields...

        )
    }

    private fun disableUserInteraction() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun enableUserInteraction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
