import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TxHandler {

	private UTXOPool utxoPool; 
	
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
    	this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,  -
     * (2) the signatures on each input of {@code tx} are valid, - 
     * (3) no UTXO is claimed multiple times by {@code tx}, -
     * (4) all of {@code tx}s output values are non-negative, and -
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     *     
     *  For the input to be valid, the signature it contains must be a valid signature over the 
     *  current transaction with the public key in the spent output.
     */
    public boolean isValidTx(Transaction tx) {
    	System.out.println(tx.toString());
    	int txSum = 0; // sum of {@code tx}s input values
    	int outputSum = 0; // sum of output values
    	ArrayList<UTXO> utxos = utxoPool.getAllUTXO(); // Iterable list of all UTXOs from utxoPool
    	ArrayList<Transaction.Input> txInputs = tx.getInputs(); // iterable list of all imputs
    	Map<byte[], Integer> claimCount = new HashMap<byte[], Integer>(); // how many times a tx matches tx's in the UTXO pool
    	
    	// check transactions are claimed in current UTXO pool
    	for (Transaction.Input txo: txInputs) { // iterate over all inputs
    		
    		double txValue = tx.getOutput(txo.outputIndex).value; // the tx's 'input value'

    		if (txValue < 0) {
    			return false;
    		}
    		
    		txSum += txValue;
    		
	   		 // check signatures on each input of {@code tx} are valid
	   		if (!(Crypto.verifySignature(tx.getOutput(txo.outputIndex).address, txo.prevTxHash, txo.signature))) {
	   			return false;
	   		}
	   		
    		for (UTXO utxo: utxos) { // n^2 matching of transaction to UTXO
    			UTXO inputUTXO = new UTXO(txo.prevTxHash, txo.outputIndex);
    			
	    		if (utxo.equals(inputUTXO) == true) {
	    			if (claimCount.containsKey(txo.prevTxHash)) {
	    				//claim exists, add 1 to the number of times it was found
	    				claimCount.put(txo.prevTxHash, claimCount.get(txo.prevTxHash)+1);
	    			} else {
	    				//claim doesn't exist in cache, create it and set to 1 occurance
	    				claimCount.put(txo.prevTxHash, 1);
	    			}
	    		}
	    		outputSum += tx.getOutput(utxo.getIndex()).value; 
	   		}
    		
    		if (claimCount.get(txo.prevTxHash) > 1) {
    			// tx claimed more than once in UTXO pool
    			return false;
    		}
    		
    		if (txSum < outputSum){
    			//the sum of {@code tx}s input values is not greater than or equal to the sum of its output values
    			return false;
    		}
    		
    		
    		
    	}
    	
    	for (Map.Entry<byte[], Integer> claim: claimCount.entrySet()) {
	    	if (claim.getValue() == 0) {
	    		// tx not claimed in UTXO pool
	    		return false;
	    	}
    	}
    	
    	
    	
    	return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	return null;
    }

}
