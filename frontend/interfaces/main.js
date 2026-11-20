document.querySelectorAll('[data-confirmar]').forEach(function(f){
  f.addEventListener('submit',function(e){e.preventDefault();alert(f.dataset.confirmar);});
});
var fecha=document.getElementById('fecha');
if(fecha){fecha.min=new Date().toISOString().split('T')[0];}
