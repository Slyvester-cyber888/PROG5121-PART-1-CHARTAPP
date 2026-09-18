/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.iie.prog5121.icetask3.chatapppart_1;

/**
 *
 * @author Student
 */
public class LogIn {
    //Declare variable that are givng to the used by the user   
    
   String username; 
   String password;
   String phoneNumber;
    
  //Username validation
   public boolean checkUserName(String username) {
   return username.contains("_")&& username.length() <=5;
   }
   public boolean checkPasswordComplexity(String password) {
   
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        
        for (int i = 0; i < passsword.length(); i++) {
            char c = password.charAt(i);
            
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }   
return password.length() >= 8 && hasCapital && hasNumber && hasSpecial;
   
   }
//cellphone validation, should contain +27 and is no more than 12 characters
   public boolean checkCellPhoneNumber(String phone) {
       return phone.startsWith("+27") && phone.length() <=12;
   }
    
 public String registerUser(String username, String password, String phoneNumber){
   
       if(!checkUserName(username)){
       return "Üsername is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
       }
       
       if(!checkPasswordComplexity(password)){
       return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
       }
       
       if(!checkCellPhoneNumber(phoneNumber)){
       return "Cell phone number incorrectly formatted or does not contain international code.";
       }
       
       this.username = username;
       this.password = password;
       this.phoneNumber = phoneNumber;
       
       return "User registered successfully";
   }
 //Login feature
   public boolean loginUser(String username, String password){
   return this.username.equals(username)&& this.password.equals(password);
   }
public String returnLoginStatus(boolean success){
       if(success){
           return "Welcome" + username + "it is great to see you again.";
       }else{
           return "Username or password incorrect, please try again.";
       }
       
       public String getUsername{} {
           return username;
        }

        public String getCellPhoneNumber{} {
            return cellPhoneNumber;
        }
}
