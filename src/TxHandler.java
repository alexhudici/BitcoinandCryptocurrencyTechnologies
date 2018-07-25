import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TxHandler {

	private UTXOPool utxoPool; 
	
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     * 
     * UTXO pool represents the current funds attempting to be transferred
     * Transaction is the 
     */
    public TxHandler(UTXOPool utxoPool) {
    	this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,  -
     * (2) the signatures on each input of {@code tx} are valid, - 
     * (3) no UTXO is claimed multiple times by {@code tx}, -
     * (4) all of {@code tx}s output values are non-negative, and 
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     *     
     *  For the input to be valid, the signature it contains must be a valid signature over the 
     *  current transaction with the public key in the spent output.
     */
    public boolean isValidTx(Transaction tx) {
    	double inputSum = 0; // sum of {@code tx}s input values
    	double outputSum = 0; // sum of output values
    	ArrayList<UTXO> utxos = utxoPool.getAllUTXO(); // Iterable list of all UTXOs from utxoPool - necessary?
    	ArrayList<Transaction.Input> txInputs = tx.getInputs(); // iterable list of all imputs
    	ArrayList<Transaction.Output> txOutputs = tx.getOutputs(); // iterable list of all outputs
    	HashSet<UTXO> utxosClaimed = new HashSet<UTXO>(); // how many times a tx matches tx's in the UTXO pool

    	int in = 0;
    	// check transactions are claimed in current UTXO pool
    	for (Transaction.Input txo: txInputs) { // iterate over all inputs

    		// the UTXO that this transaction input (old unspent output/money unit) references
    		UTXO output = new UTXO(txo.prevTxHash, txo.outputIndex); 

    		// get the output in the UTXO pool using the prev transaction hash provided & output index in the input of
    		// this transaction
    		Transaction.Output transactionOutput = utxoPool.getTxOutput(output);

    		if (transactionOutput == null) // does transaction exist in UTXO pool? - faster variant
    		// if (!utxoPool.contains(output)) slower variant
    			return false;
 
    		double txValue = transactionOutput.value; // the tx's 'input value' 

    		if (txValue < 0) {
    			System.out.println("txvalue is less than 0: " + txValue);
    			return false;
    		}

	   		// check signatures on each input of {@code tx} are valid
    		// the signature it contains must be a valid signature over the 
    	    // current transaction with the public key in the spent output.
//	   		if (!(Crypto.verifySignature(transactionOutput.address, tx.getRawDataToSign(in), txo.signature))) {
//	   			return false;
//	   		}

    		// to test locally
    		RSAKey address = transactionOutput.address;
	   		if (!address.verifySignature(tx.getRawDataToSign(in), txo.signature)) {
	   			//System.out.println("signature is bad");
	            return false;
	   		}

    		inputSum += txValue;

    		if (!utxosClaimed.add(output)) {
    		// found inputs in the tx that try to reference a UTXO (available fund unit) twice
    		//	System.out.println("double spend found");
                return false;
             }

			in++;
    	}

    	for (Transaction.Output txo: txOutputs) {
			if (txo.value < 0)
				return false;
    		outputSum += txo.value;
		}

		if (inputSum < outputSum){
			//the sum of {@code tx}s input values is not greater than or equal to the sum of its output values
			//System.out.println("fff, " + inputSum + " and output " + outputSum);
			return false;
		}

    	return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        Transaction[] acceptedTrans = new Transaction[possibleTxs.length];
    	for (int i = 0; i < possibleTxs.length; i++) {
    		if(isValidTx(possibleTxs[i])) {
    			acceptedTrans[i] = possibleTxs[i];
    		} else {
    			updateUTXOPool(possibleTxs[i]);
    		}
    	}
        return acceptedTrans;
    }

    private boolean updateUTXOPool(Transaction tr) {
    	utxoPool.
    }

}
