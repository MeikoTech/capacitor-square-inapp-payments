import Foundation
import Capacitor
import SquareInAppPaymentsSDK
import PassKit



/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorSquareInappPaymentsPlugin)

public class CapacitorSquareInappPaymentsPlugin: CAPPlugin {
    
    @objc func startGooglePay(_ call: CAPPluginCall) {
        call.unimplemented("Not implemented on iOS.")
    }
  
    @objc func startApplePay(_ call: CAPPluginCall) {
        let chargeAmountString = call.getString("chargeAmount")
        let countryCode = call.getString("countryCode")
        let currencyCode = call.getString("currencyCode")
        let merchantId = call.getString("merchantId")
        
        if (chargeAmountString == nil) {
            call.resolve(["cardEntryFailed": "applePay", "message" : "chargeAmount invalid"])
            return
        }
        
        let chargeAmount = Decimal(string: chargeAmountString!)
        
        if (countryCode == nil) {
            call.resolve(["cardEntryFailed": "applePay", "message" : "countryCode invalid"])
        }
        
        if (currencyCode == nil) {
            call.resolve(["cardEntryFailed": "applePay", "message" : "currencyCode invalid"])
        }
        
        if (merchantId == nil) {
            call.resolve(["cardEntryFailed": "applePay", "message" : "merchantId invalid"])
            return
        }    
        
        DispatchQueue.main.async {
            let viewController = self.bridge?.viewController
            guard viewController != nil else {
                call.reject("Failed to retrieve view controller.")
                return
            }

            self.requestApplePayAuthorization(chargeAmount: chargeAmount!, countryCode: countryCode ?? "", currencyCode: currencyCode ?? "", merchantIdentifier: merchantId ?? "")
            call.resolve(["cardEntryStarted": "applePay"])
        }
    }

    @objc func startCardPayment(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            let viewController = self.bridge?.viewController

            let cardEntry = self.makeCardEntryViewController()
            cardEntry.delegate = self

            let nc = UINavigationController(rootViewController: cardEntry)
            viewController?.present(nc, animated: true, completion: nil)
            
            call.resolve(["cardEntryStarted": "cardEntry"])
        }
    }

    private func makeCardEntryViewController() -> SQIPCardEntryViewController {
        let theme = SQIPTheme();
        let cardEntry = SQIPCardEntryViewController(theme: theme) // Set your desired theme here
        cardEntry.collectPostalCode = true
        return cardEntry
    }
}

// Handle the card entry success or failure from the card entry form
extension CapacitorSquareInappPaymentsPlugin: SQIPCardEntryViewControllerDelegate {
    public func cardEntryViewController(
        _ viewController: SQIPCardEntryViewController,
        didCompleteWith status: SQIPCardEntryCompletionStatus
    ) {
        // Handle completion status here
        // You can use self.bridge to communicate back to the JavaScript side if needed

        viewController.dismiss(animated: true, completion: nil)
    }

    public func cardEntryViewController(
        _ viewController: SQIPCardEntryViewController,
        didObtain cardDetails: SQIPCardDetails,
        completionHandler: @escaping (Error?) -> Void
    ) {
        // Handle card details here
        // You can use self.bridge to communicate back to the JavaScript side if needed
        
        let data: [String: Any] = [
            "paymentMethod": "cardEntry",
            "nonce": cardDetails.nonce,
            "cardBrand": cardDetails.card.brand.description,
            "cardLastFour": cardDetails.card.lastFourDigits
        ]

        notifyListeners("cardDetailsSuccess", data: data)
        completionHandler(nil)
    }
}



extension CapacitorSquareInappPaymentsPlugin {
    public func requestApplePayAuthorization(chargeAmount: Decimal, countryCode: String, currencyCode: String, merchantIdentifier: String) {
        guard SQIPInAppPaymentsSDK.canUseApplePay else {
            print("Apple Pay Not Enabled")
      
            self.notifyListeners("cardDetailsFailed", data: ["message": "Apple Pay not enabled or no support card found!"])

            return
        }
        let paymentRequest = PKPaymentRequest.squarePaymentRequest(
            merchantIdentifier: "merchant.com.spyce.delivery",
            countryCode: "CA",
            currencyCode: "CAD"
        )

        paymentRequest.paymentSummaryItems = [
            PKPaymentSummaryItem(label: "Testing Apple Pay", amount: NSDecimalNumber(decimal: chargeAmount)),
        ]

        let paymentAuthorizationViewController =
            PKPaymentAuthorizationViewController(paymentRequest: paymentRequest)

        paymentAuthorizationViewController!.delegate = self

        let viewController = self.bridge?.viewController
        viewController?.present(paymentAuthorizationViewController!, animated: true, completion: nil)
    }
}

extension CapacitorSquareInappPaymentsPlugin: PKPaymentAuthorizationViewControllerDelegate {
    public func paymentAuthorizationViewController(
        _: PKPaymentAuthorizationViewController,
        didAuthorizePayment payment: PKPayment,
        handler completion: @escaping (PKPaymentAuthorizationResult) -> Void
    ) {
        let nonceRequest = SQIPApplePayNonceRequest(payment: payment)
        nonceRequest.perform { cardDetails, error in
            if let cardDetails = cardDetails {
                let cardNonceString = cardDetails.nonce
                let cardBrandString = cardDetails.card.brand.description
                let cardLastFourString = cardDetails.card.lastFourDigits

                let data: [String: Any] = [
                    "paymentMethod": "applePay",
                    "nonce": cardNonceString,
                    "cardBrand": cardBrandString,
                    "cardLastFour": cardLastFourString
                ]

                self.notifyListeners("cardDetailsSuccess", data: data)
                completion(PKPaymentAuthorizationResult(status: .success, errors: nil))
            } else if let error = error {
                completion(PKPaymentAuthorizationResult(status: .failure, errors: [error]))
            }
        }
    }

    public func paymentAuthorizationViewControllerDidFinish(
        _: PKPaymentAuthorizationViewController
    ) {
        let viewController = self.bridge?.viewController
        viewController?.dismiss(animated: true, completion: nil)
    }
}






