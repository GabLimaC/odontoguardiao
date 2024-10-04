import android.util.Base64
import android.util.Log
import com.example.odontoguardio.User
import com.example.odontoguardio.Utilidade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseManager {
    private suspend fun getConnection(): Connection? = withContext(Dispatchers.IO) {
        try {
            Class.forName("org.postgresql.Driver")
            DriverManager.getConnection(
                "jdbc:postgresql://bubble.db.elephantsql.com:5432/nbzrzzgs",
                "nbzrzzgs",
                "G2pP0e7VL7ysfJ1T-fYIP-GUPsZcYM-Q"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createUser(nome: String, sobrenome: String, email: String, senha: String): Boolean = withContext(Dispatchers.IO) {
        val connection = getConnection() ?: return@withContext false
        var success = false
        try {
            val existinguser = readUser(email)
            if (existinguser != null){
                return@withContext false
            }
        }catch (e: SQLException){
            e.printStackTrace()
        }

        try {
            val sql = "INSERT INTO usuarios (nome, sobrenome, email, senha) VALUES (?, ?, ?, ?)"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, nome)
                stmt.setString(2, sobrenome)
                stmt.setString(3, email)
                stmt.setString(4, senha)
                success = stmt.executeUpdate() > 0
                return@withContext true
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        success
    }

    suspend fun readUser(email: String): User? = withContext(Dispatchers.IO) {
        val connection = getConnection() ?: return@withContext null
        var user: User? = null
        try {
            val sql = "SELECT nome, sobrenome, email FROM usuarios WHERE email = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                val resultSet = stmt.executeQuery()
                if (resultSet.next()) {
                    user = User(
                        nome = resultSet.getString("nome"),
                        sobrenome = resultSet.getString("sobrenome"),
                        email = resultSet.getString("email"),
                    )
                    return@withContext user
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        user
    }

    suspend fun readUserName(email: String): String? = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        try {
            connection = getConnection() ?: return@withContext null
            val sql = "SELECT nome FROM usuarios WHERE email = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                stmt.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        return@withContext resultSet.getString("nome")
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        null
    }

    suspend fun updateUser(email: String, nome: String, sobrenome: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        try {
            connection = getConnection() ?: return@withContext Pair(false, "Failed to establish database connection")
            val sql = "UPDATE usuarios SET nome = ?, sobrenome = ? WHERE email = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, nome)
                stmt.setString(2, sobrenome)
                stmt.setString(3, email)
                val rowsAffected = stmt.executeUpdate()
                return@withContext if (rowsAffected > 0) {
                    Pair(true, "Update successful")
                } else {
                    Pair(false, "No rows updated. User with email $email not found.")
                }
            }
        } catch (e: SQLException) {
            Log.e("UpdateUser", "SQLException during update", e)
            return@withContext Pair(false, "Database error: ${e.message}")
        } finally {
            try {
                connection?.close()
            } catch (e: SQLException) {
                Log.e("UpdateUser", "Error closing connection", e)
            }
        }
    }



    suspend fun deleteUser(email: String): Boolean = withContext(Dispatchers.IO) {
        val connection = getConnection() ?: return@withContext false
        var success = false
        try {
            val sql = "DELETE FROM usuarios WHERE email = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                success = stmt.executeUpdate() > 0
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        success
    }
    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val connection = getConnection() ?: return@withContext false
        var isValid = false
        try {
            val sql = "SELECT senha FROM usuarios WHERE email = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                val resultSet = stmt.executeQuery()
                if (resultSet.next()) {
                    val storedPassword = resultSet.getString("senha")
                    isValid = Utilidade().checkPassword(password, storedPassword)
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        isValid
    }

    suspend fun storeRecoveryCode(email: String, recoveryCode: String): Boolean = withContext(Dispatchers.IO) {
        // Attempt to get a connection; return false if unable to connect
        val connection = getConnection() ?: return@withContext false

        // Use the connection resource properly to ensure it's closed after use
        connection.use { conn ->
            try {
                // Single call to check if the user exists
                val user = readUser(email)
                if (user == null) {
                    return@withContext false // User not found
                }

                // Prepare the SQL statement to update the recovery code
                val sql = "UPDATE usuarios SET codigo = ? WHERE email = ?"
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, recoveryCode)
                    stmt.setString(2, email)

                    // Execute the update and return true if at least one row was updated
                    val rowsUpdated = stmt.executeUpdate()
                    return@withContext rowsUpdated > 0
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                return@withContext false // Return false in case of an SQL exception
            }
        }
    }


    suspend fun submitRecoveryCode(email: String, code: String): Boolean = withContext(Dispatchers.IO) {
        val connection = getConnection() ?: return@withContext false
        var isValid = false
        try {
            val sql = "SELECT codigo FROM usuarios WHERE email = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, email)
                val resultSet = stmt.executeQuery()
                if (resultSet.next()) {
                    val storedCode = resultSet.getString("codigo")
                    isValid = storedCode == code
                    return@withContext isValid
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        return@withContext isValid
    }

    suspend fun sendRecoveryEmail(email: String, recoveryCode: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // **Security Note:** It's highly recommended to fetch these from a secure source
            val apiKey = "2aff0c51877973ff2295d001bd75d0f8"
            val apiSecret = "24794bd3429d3b1c70bb27caad095315"

            val client = OkHttpClient()

            // Construct the JSON payload
            val jsonBody = """
            {
                "Messages": [
                    {
                        "From": {
                            "Email": "odontoguardiao@mail.com",
                            "Name": "Odontoguardião"
                        },
                        "To": [
                            {
                                "Email": "$email",
                                "Name": "User"
                            }
                        ],
                        "Subject": "Password Recovery",
                        "TextPart": "Your recovery code is $recoveryCode.",
                        "HTMLPart": "<h3>Your recovery code is <strong>$recoveryCode</strong>.</h3>"
                    }
                ]
            }
        """.trimIndent()

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = jsonBody.toRequestBody(mediaType)

            // Build the HTTP request
            val request = Request.Builder()
                .url("https://api.mailjet.com/v3.1/send")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .header("Authorization", Credentials.basic(apiKey, apiSecret))
                .build()

            // Execute the request
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    // Optionally, parse the response body for more details
                    return@withContext true
                } else {
                    // Log the error response for debugging
                    println("Mailjet API Error: ${response.code} - ${response.message}")
                    println("Response Body: ${response.body?.string()}")
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun sendEmailWithAttachment(pdfBytes: ByteArray, email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val apiKey = "2aff0c51877973ff2295d001bd75d0f8"
            val apiSecret = "24794bd3429d3b1c70bb27caad095315"

            // Base64 encode the API Key and Secret for Basic Authentication
            val credentials = "$apiKey:$apiSecret"
            val auth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

            // Read and encode the PDF file
            val pdfBase64 = Base64.encodeToString(pdfBytes, Base64.NO_WRAP)

            // Create the JSON payload
            val emailData = JSONObject().apply {
                put("Messages", org.json.JSONArray().put(
                    JSONObject().apply {
                        put("From", JSONObject().apply {
                            put("Email", "odontoguardiao@mail.com")
                            put("Name", "Odontoguardião")
                        })
                        put("To", org.json.JSONArray().put(
                            JSONObject().apply {
                                put("Email", email) // Replace with recipient's email
                                put("Name", "Recipient Name")
                            }
                        ))
                        put("Subject", "Nova denúncia")
                        put("TextPart", "Uma nova denúncia anônima foi realizada usando o aplicativo Odontoguardião, por favor verifique os anexos")
                        put("HTMLPart", "<h3>Verifique os anexos desse email.</h3>")
                        put("Attachments", org.json.JSONArray().put(
                            JSONObject().apply {
                                put("ContentType", "application/pdf")
                                put("Filename", "document.pdf")
                                put("Base64Content", pdfBase64)
                            }
                        ))
                    }
                ))
            }

            // Create the request body
            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = emailData.toString().toRequestBody(mediaType)

            // Build the request
            val request = Request.Builder()
                .url("https://api.mailjet.com/v3.1/send")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", auth)
                .post(requestBody)
                .build()

            // Create OkHttpClient instance
            val client = OkHttpClient()

            // Execute the request
            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun updatePassword(email: String, newPass: String): Boolean = withContext(Dispatchers.IO) {
        // Attempt to get a connection; return false if unable to connect
        val connection = getConnection() ?: return@withContext false

        // Use the connection resource properly to ensure it's closed after use
        connection.use { conn ->
            try {
                // Single call to check if the user exists
                val user = readUser(email)
                if (user == null) {
                    return@withContext false // User not found
                }

                // Prepare the SQL statement to update the recovery code
                val sql = "UPDATE usuarios SET senha = ? WHERE email = ?"
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, newPass)
                    stmt.setString(2, email)

                    // Execute the update and return true if at least one row was updated
                    val rowsUpdated = stmt.executeUpdate()
                    return@withContext rowsUpdated > 0
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                return@withContext false // Return false in case of an SQL exception
            }
        }
    }


}