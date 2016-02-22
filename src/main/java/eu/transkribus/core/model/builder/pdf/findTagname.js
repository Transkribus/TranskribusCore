function findTagname(name) {
if (search.available) {
search.wordMatching = "MatchPhrase";
search.matchWholeWord = true;
search.query(name, "ActiveDoc");
}
else {
app.alert("The Search plug-in isn't installed.");
}
}