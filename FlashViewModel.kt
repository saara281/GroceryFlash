package com.internshala.flash.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internshala.flash.Network.FlashApi
import com.internshala.flash.data.InternetItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlashViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(FlashUiState())
    val uiState : StateFlow<FlashUiState> = _uiState.asStateFlow()

    val _isVisible = MutableStateFlow<Boolean>(true)
    val isVisible = _isVisible

    var itemUiState : ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set

    private val  _user = MutableStateFlow<FirebaseUser?>(null)
    val user : MutableStateFlow<FirebaseUser?>get() = _user

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber  : MutableStateFlow<String>get() = _phoneNumber

    private val _otp = MutableStateFlow(" ")
    val otp : MutableStateFlow<String>get() = _otp

    private val _verificationId = MutableStateFlow(" ")
    val verificationId : MutableStateFlow<String> get() = _verificationId

    private val _ticks = MutableStateFlow(60L)
    val ticks : MutableStateFlow<Long> get() = _ticks

    private val _loading = MutableStateFlow(false)
    val loading : MutableStateFlow<Boolean> get() = _loading

    private val _logoutClicked = MutableStateFlow(false)
    val logoutClicked : MutableStateFlow<Boolean> get() = _logoutClicked

    private lateinit var timerJob : Job

    val database = Firebase.database
    val myRef = database.getReference("message")

    private val _cartItems = MutableStateFlow<List<InternetItem>>(emptyList())
    val cartItems : StateFlow<List<InternetItem>>get() = _cartItems.asStateFlow()

    lateinit var internetJob : Job
    lateinit var screenJob : Job

    sealed interface ItemUiState{
        data class Success ( val items : List<InternetItem>) : ItemUiState
        object Loading : ItemUiState
        object Error : ItemUiState

    }

    fun setPhoneNumber(s: String) {
        _phoneNumber.value = phoneNumber.toString()
    }

    fun setOtp(otp:String){
        _otp.value = otp
    }

    fun setVerificationId(verificationId:String){
        _verificationId.value = verificationId
    }

    fun clearData(){
        _user.value = null
        _phoneNumber.value = " "
        _otp.value = " "
        verificationId.value = " "
        resetTimer()
    }

    fun runTimer(){
        timerJob = viewModelScope.launch{
            while (_ticks.value>0){
                delay(1000)
                _ticks.value -= 1
            }
        }
    }

    fun resetTimer(){
        try{
            timerJob.cancel()
        }catch(exception:Exception){

        }finally {
            _ticks.value = 60
        }
    }

    fun setLoading(isLoading : Boolean){
        _loading.value = isLoading
    }

    fun setLogoutStatus(
        logoutStatus: Boolean
    ){
        _logoutClicked.value = logoutStatus
    }

    fun setUser(user: FirebaseUser){
        _user.value = user
    }
     
    fun addToCart(item:InternetItem){
        _cartItems.value = _cartItems.value + item
    }

    fun addToDatabase(item: InternetItem){
        myRef.push().setValue(item)

    }

    fun fillCartItems(){
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                _cartItems.value = emptyList()
                for(childSnapshots in dataSnapshot.children){
                    val item = childSnapshots.getValue(InternetItem::class.java)
                    item?.let {
                        val newItem = it
                        addToCart(newItem)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun removeFromCart(olditem: InternetItem){
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                for(childSnapshot in datasnapshot.children){
                    var itemRemoved = false
                    val item = childSnapshot.getValue(InternetItem::class.java)
                    item?.let{
                        if(olditem.itemName == it.itemName && olditem.itemPrice == it.itemPrice){
                            childSnapshot.ref.removeValue()
                            itemRemoved = true
                        }
                    }
                    if(itemRemoved) break
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        )
    }

    fun updateClickText(updatedText:String){
        _uiState.update {
            it.copy(
                clickStatus = updatedText
            )
        }
    }
    fun updateSelectedCategory(updatedCategory: Int){
        _uiState.update {
            it.copy(
                selectCategory = updatedCategory
            )
        }
    }
    fun toggleVisibility(){
        _isVisible.value = false
    }
    fun getFlashItems(){
        internetJob = viewModelScope.launch{
            try {
                val listResult = FlashApi.retrofitService.getItems()
                itemUiState = ItemUiState.Success(listResult)
            }
            catch (exception:Exception){
                itemUiState = ItemUiState.Error
                toggleVisibility()
                screenJob.cancel()

            }

        }
    }


    init {
        screenJob = viewModelScope.launch(Dispatchers.Default){
            delay(3000)
            toggleVisibility() 
        }
        getFlashItems()
        fillCartItems()
    }
}