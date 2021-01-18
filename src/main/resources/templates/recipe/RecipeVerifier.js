function setInputFilter(textbox, inputFilter) {
    if (textbox == null)
        return;

    ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop"].forEach(function(event) {
        textbox.addEventListener(event, function() {
            if (inputFilter(this.value)) {
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;
            } else if (this.hasOwnProperty("oldValue")) {
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                this.value = "";
            }
        });
    });
}

// Integer >=0
setInputFilter(document.getElementById("prepTime"), function (value) {
    return /^\d*$/.test(value);
});

// Integer >=0
setInputFilter(document.getElementById("cookTime"), function (value) {
    return /^\d*$/.test(value);
});