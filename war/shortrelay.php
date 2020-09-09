<?php
// 
// This module acts as a relay to any URL shortener with a suitable API
// update API call as needed if using API other than tinyurl.com
//
//
    $serveraddr='http' . (isset($_SERVER['HTTPS']) ? 's' : '') . '://' . "{$_SERVER['HTTP_HOST']}";
	$s=parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
	$path= str_replace('shortrelay.php','circuitjs.html',$s);
	$ch = curl_init();
	// encode entire url.  tinyurl won't decode the url unless the entire thing is urlencoded.
	// we need to encode it because otherwise tinyurl gives us an error if the url includes %0A's (which it always does)
	$v=urlencode($serveraddr . $path . $_GET["v"]);
	curl_setopt($ch, CURLOPT_URL, 'http://tinyurl.com/api-create.php?url='. $v);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	echo curl_exec($ch);
//    echo $serveraddr . $path .$v;
	exit;
?>
