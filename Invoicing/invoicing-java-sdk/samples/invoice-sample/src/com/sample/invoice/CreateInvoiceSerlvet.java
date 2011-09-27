package com.sample.invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;
import com.paypal.svcs.services.InvoiceService;
import com.paypal.svcs.types.common.AckCode;
import com.paypal.svcs.types.common.ErrorData;
import com.paypal.svcs.types.common.RequestEnvelope;
import com.paypal.svcs.types.pt.CreateAndSendInvoiceRequest;
import com.paypal.svcs.types.pt.CreateAndSendInvoiceResponse;
import com.paypal.svcs.types.pt.CreateInvoiceRequest;
import com.paypal.svcs.types.pt.CreateInvoiceResponse;
import com.paypal.svcs.types.pt.InvoiceItemListType;
import com.paypal.svcs.types.pt.InvoiceItemType;
import com.paypal.svcs.types.pt.InvoiceType;
import com.paypal.svcs.types.pt.PaymentTermsType;

/**
 * Servlet implementation class CreateInvoiceSerlvet
 */
public class CreateInvoiceSerlvet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateInvoiceSerlvet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		getServletConfig().getServletContext()
				.getRequestDispatcher("/CreateInvoice.jsp")
				.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		InvoiceType invoiceType = new InvoiceType();
		invoiceType.setMerchantEmail(request.getParameter("merchantEmail"));
		invoiceType.setPayerEmail(request.getParameter("payerEmail"));
		List<InvoiceItemType> items = new ArrayList<InvoiceItemType>();
		InvoiceItemListType invoiceItem = new InvoiceItemListType();
		items.add(new InvoiceItemType(request.getParameter("item_name1"),
				Double.valueOf(request.getParameter("item_quantity1")), Double
						.valueOf(request.getParameter("item_unitPrice1"))));
		items.add(new InvoiceItemType(request.getParameter("item_name2"),
				Double.valueOf(request.getParameter("item_quantity2")), Double
						.valueOf(request.getParameter("item_unitPrice2"))));
		invoiceItem.setItem(items);
		invoiceType.setItemList(invoiceItem);

		invoiceType.setCurrencyCode(request.getParameter("currencyCode"));
		invoiceType.setPaymentTerms(PaymentTermsType.valueOf(request
				.getParameter("paymentTerms")));

		RequestEnvelope env = new RequestEnvelope("en_US");
		try {
			response.setContentType("text/html");
			if (request.getParameter("CreateBtn") != null) {
				if (request.getParameter("CreateBtn").equals("CreateInvoice")) {
					CreateInvoiceRequest createRequest = new CreateInvoiceRequest(
							env, invoiceType);
					InvoiceService invoiceSrvc = new InvoiceService(this
							.getServletContext().getRealPath("/")
							+ "/WEB-INF/sdk_config.properties");

					if (request.getParameter("accessToken") != null
							&& request.getParameter("tokenSecret") != null) {
						invoiceSrvc.setAccessToken(request
								.getParameter("accessToken"));
						invoiceSrvc.setTokenSecret(request
								.getParameter("tokenSecret"));

					}
					CreateInvoiceResponse createResp = invoiceSrvc
							.createInvoice(createRequest);

					response.getWriter()
							.println(
									"<br/>Ack:"
											+ createResp.getResponseEnvelope()
													.getAck());
					if (createResp.getResponseEnvelope().getAck()
							.equals(AckCode.SUCCESS)) {
						response.getWriter().println(
								"<br/>InvoiceID:" + createResp.getInvoiceID());
						response.getWriter().println(
								"<br/>InvoiceNumber:"
										+ createResp.getInvoiceNumber());
					} else {
						Iterator iterator = createResp.getError().iterator();
						while (iterator.hasNext()) {
							ErrorData error = (ErrorData) iterator.next();
							response.getWriter().println(
									"<br/>" + error.getMessage());
						}
					}
				} else if (request.getParameter("CreateBtn").equals(
						"CreateAndSendInvoice")) {
					CreateAndSendInvoiceRequest createRequest = new CreateAndSendInvoiceRequest(
							env, invoiceType);
					InvoiceService invoiceSrvc = new InvoiceService(this
							.getServletContext().getRealPath("/")
							+ "/WEB-INF/sdk_config.properties");

					CreateAndSendInvoiceResponse createResp = invoiceSrvc
							.createAndSendInvoice(createRequest);
					response.getWriter()
							.println(
									"<br/>Ack:"
											+ createResp.getResponseEnvelope()
													.getAck());
					if (createResp.getResponseEnvelope().getAck()
							.equals(AckCode.SUCCESS)) {
						response.getWriter().println(
								"<br/>InvoiceID:" + createResp.getInvoiceID());
						response.getWriter().println(
								"<br/>InvoiceNumber:"
										+ createResp.getInvoiceNumber());
					} else {
						Iterator iterator = createResp.getError().iterator();
						while (iterator.hasNext()) {
							ErrorData error = (ErrorData) iterator.next();
							response.getWriter().println(
									"<br/>" + error.getMessage());
						}
					}
				} else {
					response.getWriter().println(
							"Unknown request - click one of the buttons");
				}
			}
			response.getWriter().println("<br/><a href='index.html'>Home</a>");
		} catch (SSLConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResponseDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientActionRequiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
