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
	$v=$_GET["v"]; // Note this removes one layer of URL encoding from the param. This works for tinyurl.com but may not for all!
	curl_setopt($ch, CURLOPT_URL, 'http://tinyurl.com/api-create.php?url='. $serveraddr . $path . $v);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	echo curl_exec($ch);
//    echo $serveraddr . $path .$v;
	exit;
?>