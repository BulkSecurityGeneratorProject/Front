package io.github.jhipster.application.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.application.domain.PaymentMethod;

import io.github.jhipster.application.repository.PaymentMethodRepository;
import io.github.jhipster.application.repository.search.PaymentMethodSearchRepository;
import io.github.jhipster.application.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.application.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing PaymentMethod.
 */
@RestController
@RequestMapping("/api")
public class PaymentMethodResource {

    private final Logger log = LoggerFactory.getLogger(PaymentMethodResource.class);

    private static final String ENTITY_NAME = "paymentMethod";

    private final PaymentMethodRepository paymentMethodRepository;

    private final PaymentMethodSearchRepository paymentMethodSearchRepository;

    public PaymentMethodResource(PaymentMethodRepository paymentMethodRepository, PaymentMethodSearchRepository paymentMethodSearchRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodSearchRepository = paymentMethodSearchRepository;
    }

    /**
     * POST  /payment-methods : Create a new paymentMethod.
     *
     * @param paymentMethod the paymentMethod to create
     * @return the ResponseEntity with status 201 (Created) and with body the new paymentMethod, or with status 400 (Bad Request) if the paymentMethod has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/payment-methods")
    @Timed
    public ResponseEntity<PaymentMethod> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) throws URISyntaxException {
        log.debug("REST request to save PaymentMethod : {}", paymentMethod);
        if (paymentMethod.getId() != null) {
            throw new BadRequestAlertException("A new paymentMethod cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PaymentMethod result = paymentMethodRepository.save(paymentMethod);
        paymentMethodSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/payment-methods/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /payment-methods : Updates an existing paymentMethod.
     *
     * @param paymentMethod the paymentMethod to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated paymentMethod,
     * or with status 400 (Bad Request) if the paymentMethod is not valid,
     * or with status 500 (Internal Server Error) if the paymentMethod couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/payment-methods")
    @Timed
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@RequestBody PaymentMethod paymentMethod) throws URISyntaxException {
        log.debug("REST request to update PaymentMethod : {}", paymentMethod);
        if (paymentMethod.getId() == null) {
            return createPaymentMethod(paymentMethod);
        }
        PaymentMethod result = paymentMethodRepository.save(paymentMethod);
        paymentMethodSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, paymentMethod.getId().toString()))
            .body(result);
    }

    /**
     * GET  /payment-methods : get all the paymentMethods.
     *
     * @param filter the filter of the request
     * @return the ResponseEntity with status 200 (OK) and the list of paymentMethods in body
     */
    @GetMapping("/payment-methods")
    @Timed
    public List<PaymentMethod> getAllPaymentMethods(@RequestParam(required = false) String filter) {
        if ("salesorder-is-null".equals(filter)) {
            log.debug("REST request to get all PaymentMethods where salesOrder is null");
            return StreamSupport
                .stream(paymentMethodRepository.findAll().spliterator(), false)
                .filter(paymentMethod -> paymentMethod.getSalesOrder() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all PaymentMethods");
        return paymentMethodRepository.findAll();
        }

    /**
     * GET  /payment-methods/:id : get the "id" paymentMethod.
     *
     * @param id the id of the paymentMethod to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the paymentMethod, or with status 404 (Not Found)
     */
    @GetMapping("/payment-methods/{id}")
    @Timed
    public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable Long id) {
        log.debug("REST request to get PaymentMethod : {}", id);
        PaymentMethod paymentMethod = paymentMethodRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(paymentMethod));
    }

    /**
     * DELETE  /payment-methods/:id : delete the "id" paymentMethod.
     *
     * @param id the id of the paymentMethod to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/payment-methods/{id}")
    @Timed
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        log.debug("REST request to delete PaymentMethod : {}", id);
        paymentMethodRepository.delete(id);
        paymentMethodSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/payment-methods?query=:query : search for the paymentMethod corresponding
     * to the query.
     *
     * @param query the query of the paymentMethod search
     * @return the result of the search
     */
    @GetMapping("/_search/payment-methods")
    @Timed
    public List<PaymentMethod> searchPaymentMethods(@RequestParam String query) {
        log.debug("REST request to search PaymentMethods for query {}", query);
        return StreamSupport
            .stream(paymentMethodSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
