import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;

import hlib.hj.mlib.HomoAdd;
import hlib.hj.mlib.HomoDet;
import hlib.hj.mlib.HomoMult;
import hlib.hj.mlib.HomoOpeInt;
import hlib.hj.mlib.HomoRand;
import hlib.hj.mlib.HomoSearch;
import hlib.hj.mlib.PaillierKey;
import hlib.hj.mlib.RandomKeyIv;

public class generateKeys {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
    
		SecretKey DetKey = HomoDet.generateKey();
		SecretKey SearchKey = HomoSearch.generateKey();
		RandomKeyIv randomKeyIv =  HomoRand.generateKeyIv();
		PaillierKey SumKey = HomoAdd.generateKey();
		KeyPair MultkeyPair = HomoMult.generateKey();
	
		
		 try {
			 FileWriter writer = new FileWriter("chaves.txt", false);
	            BufferedWriter bufferedWriter = new BufferedWriter(writer);

	            String line = HomoDet.stringFromKey(DetKey);
	            bufferedWriter.write(line);
	            bufferedWriter.newLine();
	            
	            bufferedWriter.write(HomoSearch.stringFromKey(SearchKey));
	            bufferedWriter.newLine();
	            
	            bufferedWriter.write(HomoRand.stringFromKeyIv(randomKeyIv));
	            bufferedWriter.newLine();
	            
	            bufferedWriter.write(HomoAdd.stringFromKey(SumKey));
	            bufferedWriter.newLine();
	            
	            bufferedWriter.write(HomoMult.stringFromKey(MultkeyPair));
	            bufferedWriter.newLine();
			
	            bufferedWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
