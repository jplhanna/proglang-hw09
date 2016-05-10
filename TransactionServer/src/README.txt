Jon-Pierre Hanna hannaj2
Paul Chorba chorbp


two phase commit
run(){
	while(true){
		for(..){
			-
			-	work on cache
			-
		}try{ (first phase)
			for(i=a;i<=2;i++){
				open if needed
			}
		}catch{ clean up then continue}
		try{
			for
				verify(second phase) every acount that has been read.
		}catch{clean up then continue}
		write
		close
	}
	
	
write a small set of transactions, with a few set of outcomes. with assert true for every possible outcome.