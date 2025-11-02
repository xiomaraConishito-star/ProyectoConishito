package com.cibertec.conishitoapp.ui.store

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cibertec.conishitoapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class CheckoutFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnShowOnMap: Button
    private lateinit var wvMap: WebView
    private lateinit var rgPayment: RadioGroup
    private lateinit var btnConfirm: Button
    private lateinit var tvOrderSummary: TextView
    private lateinit var tvCheckoutTotal: TextView

    // Card fields
    private lateinit var llCardContainer: LinearLayout
    private lateinit var etCardDni: EditText
    private lateinit var etCardNumber: EditText
    private lateinit var etCardExpiry: EditText
    private lateinit var etCardCvv: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etName = view.findViewById(R.id.etName)
        etAddress = view.findViewById(R.id.etAddress)
        btnShowOnMap = view.findViewById(R.id.btnShowOnMap)
        wvMap = view.findViewById(R.id.wvMap)
        rgPayment = view.findViewById(R.id.rgPayment)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        tvOrderSummary = view.findViewById(R.id.tvOrderSummary)
        tvCheckoutTotal = view.findViewById(R.id.tvCheckoutTotal)

        llCardContainer = view.findViewById(R.id.llCardContainer)
        etCardDni = view.findViewById(R.id.etCardDni)
        etCardNumber = view.findViewById(R.id.etCardNumber)
        etCardExpiry = view.findViewById(R.id.etCardExpiry)
        etCardCvv = view.findViewById(R.id.etCardCvv)

        // set hints programmatically for card fields (layout was simplified to avoid unresolved resource warnings)
        etCardDni.hint = getString(R.string.card_dni)
        etCardNumber.hint = getString(R.string.card_number_hint)
        etCardExpiry.hint = getString(R.string.card_expiry_hint)
        etCardCvv.hint = getString(R.string.card_cvv_hint)

        updateSummary()

        // show/hide card form
        rgPayment.setOnCheckedChangeListener { _, checkedId ->
            llCardContainer.visibility = if (checkedId == R.id.rbCard) View.VISIBLE else View.GONE
        }

        // WebView setup for map
        wvMap.settings.javaScriptEnabled = true
        wvMap.settings.domStorageEnabled = true
        wvMap.webViewClient = WebViewClient()

        // show on map button
        btnShowOnMap.setOnClickListener {
            val addr = etAddress.text.toString().trim()
            if (addr.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.complete_data), Toast.LENGTH_SHORT).show()
            } else {
                lookupAddressAndShowMap(addr)
            }
        }

        // allow IME action "Done" or Enter to trigger search
        etAddress.setOnEditorActionListener { v, actionId, event ->
            val isEnter = (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            if (actionId == EditorInfo.IME_ACTION_DONE || isEnter) {
                val addr = etAddress.text.toString().trim()
                if (addr.isNotEmpty()) lookupAddressAndShowMap(addr)
                true
            } else false
        }

        // simple formatting: add spacing to card number as user types
        etCardNumber.addTextChangedListener(object : TextWatcher {
            var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true
                val digits = s.toString().replace("\\D".toRegex(), "")
                val grouped = digits.chunked(4).joinToString(" ")
                etCardNumber.setText(grouped)
                etCardNumber.setSelection(grouped.length)
                isUpdating = false
            }
        })

        // formatting for expiry: MM/AAAA and digits only
        etCardExpiry.addTextChangedListener(object : TextWatcher {
            var isUpdatingExp = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingExp) return
                isUpdatingExp = true
                val raw = s?.toString() ?: ""
                // keep only digits
                val digitsOnly = raw.replace("\\D".toRegex(), "")
                // support MM + YYYY => max 6 digits
                val truncated = if (digitsOnly.length > 6) digitsOnly.substring(0, 6) else digitsOnly

                var monthPart = ""
                var yearPart = ""
                if (truncated.length >= 2) {
                    monthPart = truncated.substring(0, 2)
                    // corregir mes si está fuera de rango
                    val monthInt = monthPart.toIntOrNull() ?: 0
                    if (monthInt < 1) monthPart = "01" else if (monthInt > 12) monthPart = "12"
                } else if (truncated.length == 1) {
                    monthPart = truncated
                }
                if (truncated.length > 2) {
                    yearPart = truncated.substring(2) // can be 1..4 digits while typing
                }

                val formatted = if (yearPart.isNotEmpty()) {
                    // ensure month is two digits
                    val m = if (monthPart.length == 1) "0$monthPart" else monthPart
                    // display full year if provided, else partial
                    "$m/${yearPart}"
                } else {
                    monthPart
                }

                etCardExpiry.setText(formatted)
                // set cursor to end
                etCardExpiry.setSelection(formatted.length.coerceAtMost(etCardExpiry.text?.length ?: 0))
                isUpdatingExp = false
            }
        })

        btnConfirm.setOnClickListener {
            onConfirmClicked()
        }
    }

    private fun lookupAddressAndShowMap(address: String) {
        lifecycleScope.launch {
            val geo = withContext(Dispatchers.IO) { geocodeAddress(address) }
            if (geo != null) {
                showMap(geo.lat, geo.lon, geo.displayName)
            } else {
                Toast.makeText(requireContext(), "No se encontró la dirección", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private data class GeoResult(val lat: Double, val lon: Double, val displayName: String)

    private fun geocodeAddress(address: String): GeoResult? {
        try {
            val q = URLEncoder.encode(address, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=$q"
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "ConishitoApp/1.0 (+https://example.com)")
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.doInput = true

            val code = conn.responseCode
            if (code != HttpURLConnection.HTTP_OK) return null
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val arr = JSONArray(text)
            if (arr.length() == 0) return null
            val obj = arr.getJSONObject(0)
            val lat = obj.getString("lat").toDouble()
            val lon = obj.getString("lon").toDouble()
            val displayName = obj.optString("display_name", address)
            return GeoResult(lat, lon, displayName)
        } catch (_: Exception) {
            return null
        }
    }

    private fun showMap(lat: Double, lon: Double, label: String) {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
              <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
              <style>html,body,#map{height:100%;margin:0;padding:0}#map{height:100%}</style>
            </head>
            <body>
              <div id="map"></div>
              <script>
                var map = L.map('map').setView([$lat, $lon], 16);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                  maxZoom: 19,
                  attribution: '&copy; OpenStreetMap contributors'
                }).addTo(map);
                L.marker([$lat, $lon]).addTo(map).bindPopup(${JSONObjectEscape(label)}).openPopup();
              </script>
            </body>
            </html>
        """
        wvMap.visibility = View.VISIBLE
        wvMap.loadDataWithBaseURL("https://", html, "text/html", "utf-8", null)
    }

    // Escape string to a quoted JavaScript string
    private fun JSONObjectEscape(s: String): String {
        val sb = StringBuilder()
        sb.append('"')
        for (ch in s) {
            when (ch) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\'' -> sb.append("\\'")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> sb.append(ch)
            }
        }
        sb.append('"')
        return sb.toString()
    }

    private fun onConfirmClicked() {
        val name = etName.text.toString().trim()
        val address = etAddress.text.toString().trim()
        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.complete_data), Toast.LENGTH_SHORT).show()
            return
        }

        if (CartManager.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.cart_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPayment = rgPayment.checkedRadioButtonId
        if (selectedPayment == -1) {
            Toast.makeText(requireContext(), getString(R.string.checkout_payment_method), Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = when (selectedPayment) {
            R.id.rbCard -> getString(R.string.payment_card)
            R.id.rbCash -> getString(R.string.payment_cash)
            else -> getString(R.string.checkout_payment_method)
        }

        // If card payment, validate card fields
        if (selectedPayment == R.id.rbCard) {
            val dni = etCardDni.text.toString().trim()
            val number = etCardNumber.text.toString().replace("\\s".toRegex(), "")
            val expiry = etCardExpiry.text.toString().trim()
            val cvv = etCardCvv.text.toString().trim()

            // DNI validation: 8 digits numeric
            if (dni.length != 8 || !dni.all { it.isDigit() }) {
                Toast.makeText(requireContext(), getString(R.string.card_dni_required), Toast.LENGTH_SHORT).show()
                return
            }
            // Only validate counts/format for testing: number length and expiry format and CVV length
            val numDigits = number.length
            if (numDigits < 12 || numDigits > 19) {
                Toast.makeText(requireContext(), "Número de tarjeta debe tener entre 12 y 19 dígitos", Toast.LENGTH_SHORT).show()
                return
            }
            if (!isExpiryFormatValid(expiry)) {
                Toast.makeText(requireContext(), "Expiración inválida (MM/AA o MM/AAAA)", Toast.LENGTH_SHORT).show()
                return
            }
            if (cvv.length !in 3..4 || !cvv.all { it.isDigit() }) {
                Toast.makeText(requireContext(), getString(R.string.card_cvv_invalid), Toast.LENGTH_SHORT).show()
                return
            }

            // Simular tokenización y pago
            lifecycleScope.launch {
                processCardPayment(number, expiry, cvv, dni)
            }

        } else {
            // Cash payment: directly register purchase
            registerPurchaseAndGoHistory(paymentMethod, name, address)
        }
    }

    private suspend fun processCardPayment(number: String, expiry: String, cvv: String, dni: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(requireContext(), getString(R.string.processing_payment), Toast.LENGTH_SHORT).show()
        }
        // Simulate tokenization network call
        val token = withContext(Dispatchers.IO) {
            delay(1200)
            // include a hash of last 4 digits of DNI + expiry + cvv (demo only) to vary token
            val meta = (dni.takeLast(4) + expiry + cvv).hashCode().toString(16)
            "tok_" + UUID.randomUUID().toString().replace("-", "").take(18) + meta.take(6)
        }

        // Simulate payment processing using token
        val success = withContext(Dispatchers.IO) {
            delay(800)
            // For demo: approve if last digit of number is even, else fail
            val lastDigit = number.lastOrNull()?.digitToIntOrNull() ?: 0
            lastDigit % 2 == 0
        }

        if (success) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), getString(R.string.payment_success), Toast.LENGTH_LONG).show()
                // Register purchase and go to history; include token for trace (demo)
                val paymentLabel = "${getString(R.string.payment_card)} • ${token.takeLast(6)} (DNI ${dni.takeLast(2)})"
                registerPurchaseAndGoHistory(paymentLabel, etName.text.toString().trim(), etAddress.text.toString().trim())
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), getString(R.string.payment_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerPurchaseAndGoHistory(paymentMethod: String, buyerName: String, address: String) {
        val items = CartManager.allItems()
        val total = CartManager.total()
        val purchase = Purchase(
            items = items,
            total = total,
            buyerName = buyerName,
            address = address,
            paymentMethod = paymentMethod
        )
        PurchaseManager.addPurchase(purchase)
        CartManager.clear()

        // Navigate to history
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PurchaseHistoryFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun updateSummary() {
        val items = CartManager.allItems()
        if (items.isEmpty()) {
            tvOrderSummary.text = getString(R.string.cart_empty)
            btnConfirm.isEnabled = false
            tvCheckoutTotal.text = getString(R.string.cart_total, 0.0)
            return
        }

        val sb = StringBuilder()
        items.forEach { ci ->
            sb.append("${ci.product.name} x${ci.quantity} - S/ ${"%.2f".format(ci.subtotal())}\n")
        }
        tvOrderSummary.text = sb.toString().trimEnd()
        tvCheckoutTotal.text = getString(R.string.cart_total, CartManager.total())
        btnConfirm.isEnabled = true
    }

    // Minimal expiry format validation (MM/AA or MM/AAAA) - do not check if expired
    private fun isExpiryFormatValid(expiry: String): Boolean {
        val cleaned = expiry.replace("\\s".toRegex(), "")
        val parts = cleaned.split("/")
        if (parts.size != 2) return false
        val month = parts[0].toIntOrNull() ?: return false
        if (month !in 1..12) return false
        val yearPart = parts[1]
        if (!(yearPart.length == 2 || yearPart.length == 4)) return false
        if (yearPart.toIntOrNull() == null) return false
        return true
    }

}
