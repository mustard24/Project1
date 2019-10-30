/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var Project1 = ( function () {
    return {
        getTable: function(){
            
            var that = this;
            $.ajax({
                url: 'registration',
                method: 'GET',
                dataType: 'html',
                data: $("#searchform").serialize(),
                success: function(response) {
                    $("#resultsarea").html(response);                    
                }
            });
        },
        register: function(){
          var that = this;
          var text = "Congrats! You have successfully registered as: ";
          $.ajax({
              url: 'registration',
              method: 'POST',
              dataType: 'json',
              data: $("#registrationform").serialize(),
              success: function(response){
                  text = text + response["name"] + "<br>";
                  text = text + " Your registration code is: <strong>" + response["registration"] +  "</strong>";
                  $("#resultsarea").html(text);               
              }
              
          });
        },
        init: function(){
            $('#version').html( "jQuery Version: " + $().jquery );

            
        }
    };
}());
