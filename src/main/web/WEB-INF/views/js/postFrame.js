function postFrame(index, post, id) {
    if (index != 3) {
        var newDiv=document.createElement('div');
        newDiv.setAttribute('class', 'insmain');
        newDiv.setAttribute('style', 'float: left;');
        newDiv.innerHTML =
            "<div class = \"inspic\">" +
            "<img src=\"" +
            post[0] +
            "alt=\"postImage\">\n" +
            "</div>\n" +
            "<div class = \"ins-body\">\n" +
            "<div class = \"ins-text\">\n" +
            post[2] +
            "</div>\n" +
            "<div class = \"text-date\">" +
            post[1] +
            "</div>\n" +
            "</div>\n" +
            "</div>";
        document.getElementById(id).appendChild(newDiv);
    }
    else {
        var newDiv=document.createElement('div');
        newDiv.setAttribute('class', 'youtmain');
        newDiv.setAttribute('style', 'float: left;');
        newDiv.innerHTML =
            "<div class = \"youtpic\">" +
            "<img src=\"" +
            post[0] +
            "alt=\"postImage\">\n" +
            "</div>\n" +
            "<div class = \"yout-body\">\n" +
            "<div class = \"yout-title\">" +
            post[1] +
            "</div>" +
            "<div class = \"text-date\">" +
            "created by&nbsp;" +
            post[4] +
            "</div>" +
            "<br>" +
            "<div class = \"text-date\">" +
            post[5] +
            "&nbsp;조회" +
            "</div>" +
            "<div class = \"yout-text\">\n" +
            post[2] +
            "</div>\n" +
            "<div class = \"text-date\">" +
            post[3] +
            "</div>\n" +
            "</div>\n" +
            "</div>";
        document.getElementById(id).appendChild(newDiv);
    }
}