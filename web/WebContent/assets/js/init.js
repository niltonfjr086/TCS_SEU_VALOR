(($)=>{
  $(()=>{

    $('.sidenav').sidenav();
    $('ul.tabs').tabs(); // {swipeable : true}
    $('.parallax').parallax();
    $('.modal').modal();
    $('.collapsible').collapsible();
    $('.fixed-action-btn').floatingActionButton();


    /**
	 * https://pt.stackoverflow.com/questions/123816/como-mudar-para-portugu%C3%AAs-o-campo-data-no-materialize
	 */
// $('.datepicker').pickadate({
//
// monthsFull : [ 'Janeiro', 'Fevereiro', 'Março', 'Abril',
// 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro',
// 'Outubro', 'Novembro', 'Dezembro' ],
// monthsShort : [ 'Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun',
// 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez' ],
// weekdaysFull : [ 'Domingo', 'Segunda', 'Terça', 'Quarta',
// 'Quinta', 'Sexta', 'Sabádo' ],
// weekdaysShort : [ 'Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex',
// 'Sab' ],
// weekdaysLetter : [ "D", "S", "T", "Q", "Q", "S", "S" ],
// today : 'Hoje',
// clear : 'Limpar',
// close : 'Pronto',
// labelMonthNext : 'Próximo mês',
// labelMonthPrev : 'Mês anterior',
// labelMonthSelect : 'Selecione um mês',
// labelYearSelect : 'Selecione um ano',
// selectMonths : true,
// selectYears : true,
// selectYears : 100,
// closeOnSelect : true
//
// });
    
// $('.slider').slider();
// $('.carousel.carousel-slider').carousel({fullWidth: true});

  }); // end of document ready
})(jQuery); // end of jQuery name space

/**
 * XML SCAPES
 * 
 * 
 * O cabeçalho opcional de todo XML é o que se segue: <?xml version="1.0"
 * encoding="ISO-8859-1"?>
 * 
 * Esses caracteres não devem ser usados como elemento, e devem ser "escapados": &,
 * use &amp; ', use &apos; ", use &quot; <, use &lt; >, use &gt;
 * 
 */

function abreModal(idModal) {
	
// $('.modal').modal('open');
	var instance = M.Modal.getInstance($("#"+idModal));
	instance.open();
}


// http://maozinhadaweb.blogspot.com.br/2014/01/mascara-generica-para-campos-numericos.html
function mascaraGenerica(evt, campo, padrao) {  
    // testa a tecla pressionada pelo usuario
    var charCode = (evt.which) ? evt.which : evt.keyCode;  
    if (charCode == 8) return true; // tecla backspace permitida
    if (charCode != 46 && (charCode < 48 || charCode > 57)) return false; // apenas
																			// numeros
    campo.maxLength = padrao.length; // Define dinamicamente o tamanho maximo
										// do campo de acordo com o padrao
										// fornecido
    // aplica a mascara de acordo com o padrao fornecido
    entrada = campo.value;  
    if (padrao.length > entrada.length && padrao.charAt(entrada.length) != '#') {
         campo.value = entrada + padrao.charAt(entrada.length);
    }
    return true;
}

// http://elcio.com.br/ajax/mascara/
function mascara(o,f){
    v_obj=o
    v_fun=f
    setTimeout("execmascara()",1)
}

function execmascara(){
    v_obj.value=v_fun(v_obj.value)
}

// https://stackoverflow.com/questions/4878756/how-to-capitalize-first-letter-of-each-word-like-a-2-word-city
function cap_frst(v){

	return v.substr(0,1).toUpperCase() + v.substr(1).toLowerCase()
}

// https://stackoverflow.com/questions/4878756/how-to-capitalize-first-letter-of-each-word-like-a-2-word-city
function capitalizer(v){

	return v.replace( /(^|\s)([a-z])/g , function(m,p1,p2){ return p1+p2.toUpperCase(); } )
}

function printTest(){
	console.log("OK");
}
