#!/usr/bin/env sh
cd /app
mb start --configfile templates/imposters.ejs --allowInjection --loglevel debug 
