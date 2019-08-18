
function createTooltipDiv (text){

    text = text.replace("[", "");
    text = text.replace("]", "");
    var selection = window.getSelection();
    var range = selection.getRangeAt(0);
    var rect = range.getBoundingClientRect();

    var div = document.createElement('div');
    div.style.border = '2px solid black';
    div.style.position = 'fixed';
    div.style.top = (rect.top - 50) + 'px';
    div.style.fontSize = '20px';
    div.style.background = 'white';
    div.style.wordBreak='break-word';
    div.style.padding = '0px 4px';
    if (!text || 0 === text.length){
        div.innerHTML = 'Brak informacji';
    }
    else{
        div.innerHTML = text;
    }
    div.style.left = rect.left + 'px';

    document.body.appendChild(div);
    var body = document.getElementsByTagName("BODY")[0];
    body.onmousemove = function () {
        div.style.display = 'none';
    }

    return div;
}


