const express = require('express')
const app = express()
const port = 3000

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', {
    title: 'Express'
  });
  res.send("/public/index.html");
});

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})