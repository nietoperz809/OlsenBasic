5 n = 0
10 for s = 0 to 255
20 n=n+1
21 print chr$(s);
22 if n <> 16 goto 30
23 n = 0
24 print
30 next s
